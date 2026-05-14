package sn.agriculteur.public_service.Client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import sn.agriculteur.public_service.Response.CollecteResponse;

import java.util.List;
import java.util.Map;

    @Component
    @Slf4j
    public class MarcheServiceClient {

        private final RestClient restClient;

        public MarcheServiceClient(
                @Value("${marche.service.base-url}") String baseUrl) {
            this.restClient = RestClient.builder()
                    .baseUrl(baseUrl)
                    .build();
        }

        // Derniers prix par produit
        public List<CollecteResponse> getDerniersPrix() {
            try {
                return restClient.get()
                        .uri("/api/marche/collectes/derniers-prix")
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<CollecteResponse>>() {});
            } catch (Exception e) {
                log.error("Erreur récupération prix : {}",
                        e.getMessage());
                return List.of();
            }
        }

        // Stock du jour
        public Map<String, Double> getStockDuJour() {
            try {
                return restClient.get()
                        .uri("/api/marche/collectes/stats/stock-du-jour")
                        .retrieve()
                        .body(new ParameterizedTypeReference<Map<String, Double>>() {});
            } catch (Exception e) {
                log.error("Erreur récupération stock : {}",
                        e.getMessage());
                return Map.of();
            }
        }
    }

