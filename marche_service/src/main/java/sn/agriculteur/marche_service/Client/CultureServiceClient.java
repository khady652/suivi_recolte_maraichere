package sn.agriculteur.marche_service.Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import sn.agriculteur.marche_service.dto.response.PrevisionClientResponse;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CultureServiceClient {

    private final RestClient restClient;

    @Value("${culture.service.url:http://culture-service}")
    private String cultureServiceUrl;

    public PrevisionClientResponse getPrevision(String produit) {
        try {
            return restClient.get()
                    .uri(cultureServiceUrl +
                            "/api/culture/previsions/" + produit)
                    .retrieve()
                    .body(PrevisionClientResponse.class);
        } catch (Exception e) {
            log.error("Erreur getPrevision : {}", e.getMessage());
            return null;
        }
    }

    public Map<String, Double> getStatsParRegion() {
        try {
            return restClient.get()
                    .uri(cultureServiceUrl +
                            "/api/culture/recoltes/stats/par-region")
                    .retrieve()
                    .body(new ParameterizedTypeReference<
                            Map<String, Double>>() {});
        } catch (Exception e) {
            log.error("Erreur getStatsParRegion : {}", e.getMessage());
            return null;
        }
    }
}