package sn.agriculture.culture_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sn.agriculture.culture_service.dtos.request.CultureRequest;
import sn.agriculture.culture_service.entity.Parcelle;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MlPredictService {

    private final RestTemplate restTemplate;

    @Value("${ml.api.url:http://localhost:5000}")
    private String mlApiUrl;

    // ============================================================
    // MÉTHODE PRINCIPALE
    // ============================================================
    public Double predireQuantiteRecolte(CultureRequest request,
                                         Parcelle parcelle) {
        try {
            // Body SANS precipitations_mm
            Map<String, Object> body = new HashMap<>();
            body.put("region",            parcelle.getLieu());
            body.put("variete",           request.getVariete());
            body.put("irrigation",        mapperIrrigation(
                    request.getTypeIrrigation()));
            body.put("qualite_sol",       parcelle.getQualiteSol());
            body.put("engrais",           Boolean.TRUE.equals(
                    request.getEngrais())
                    ? "oui" : "non");
            body.put("experience_annees", 3); // valeur par défaut
            body.put("superficie_m2",     request.getSuperficiCultive() != null
                    ? request.getSuperficiCultive()
                    : 10000.0);

            log.info("Appel API ML → {}", body);

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            // Appel Flask
            ResponseEntity<Map> response = restTemplate.exchange(
                    mlApiUrl + "/predict",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // Réponse
            if (response.getStatusCode() == HttpStatus.OK
                    && response.getBody() != null) {

                Map<String, Object> responseBody = response.getBody();
                Boolean success = (Boolean) responseBody.get("success");

                if (Boolean.TRUE.equals(success)) {
                    Object quantite = responseBody
                            .get("quantiteRecoltePrevu");
                    Double result = quantite instanceof Number
                            ? ((Number) quantite).doubleValue()
                            : null;
                    log.info("✅ Prédiction ML : {} kg", result);
                    return result;
                } else {
                    log.warn("❌ API ML erreur : {}",
                            responseBody.get("erreur"));
                    return null;
                }
            }
            return null;

        } catch (Exception e) {
            log.error("❌ Erreur appel API ML : {}", e.getMessage());
            return null;
        }
    }

    // ============================================================
    // MAPPING IRRIGATION
    // ============================================================
    private String mapperIrrigation(String typeIrrigation) {
        if (typeIrrigation == null) return "pluvial";
        switch (typeIrrigation.toLowerCase().trim()) {
            case "goutte à goutte":
            case "goutte_a_goutte":
            case "aspersion":
            case "gravitaire":
            case "lance":
                return "irrigué";
            case "mixte":
                return "mixte";
            default:
                return "pluvial";
        }
    }
}