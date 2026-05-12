package sn.agriculteur.marche_service.Client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@Slf4j
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(
            @Value("${user.service.base-url}") String userServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    // ── INFO COMPLÈTE ENQUÊTEUR ───────────────────────────
    public Map<String, String> getEnqueteurInfo(Integer id) {
        try {
            return restClient.get()
                    .uri("/api/users/enqueteurs/{id}/info", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("Erreur récupération enquêteur {} : {}",
                    id, e.getMessage());
            return null;
        }
    }

    // ── ZONE AFFECTATION ──────────────────────────────────
    public String getZoneAffectation(Integer idEnqueteur) {
        try {
            Map<String, String> info = getEnqueteurInfo(idEnqueteur);
            return info != null ? info.get("zoneAffectation") : null;
        } catch (Exception e) {
            log.error("Erreur récupération zone enquêteur {} : {}",
                    idEnqueteur, e.getMessage());
            return null;
        }
    }

    // ── ENQUÊTEUR EXISTE ──────────────────────────────────
    public boolean enqueteurExiste(Integer id) {
        try {
            restClient.get()
                    .uri("/api/users/enqueteurs/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            log.warn("Enquêteur {} introuvable : {}",
                    id, e.getMessage());
            return false;
        }
    }

    // ── ENVOYER ALERTE PRIX ───────────────────────────────
    public void envoyerAlertePrix(Map<String, String> alerteData) {
        try {
            restClient.post()
                    .uri("/api/users/alertes/prix")
                    .body(alerteData)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Alerte prix envoyée : {}",
                    alerteData.get("produit"));
        } catch (Exception e) {
            log.error("Échec envoi alerte prix : {}",
                    e.getMessage());
        }
    }
}