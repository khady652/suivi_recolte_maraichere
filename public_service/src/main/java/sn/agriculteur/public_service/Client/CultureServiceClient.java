package sn.agriculteur.public_service.Client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

    @Component
    @Slf4j
    public class CultureServiceClient {

        private final RestClient restClient;

        public CultureServiceClient(
                @Value("${culture.service.base-url}") String baseUrl) {
            this.restClient = RestClient.builder()
                    .baseUrl(baseUrl)
                    .build();
        }

        // Production par région
        public Map<String, Double> getProductionParRegion() {
            try {
                return restClient.get()
                        .uri("/api/culture/productions/par-region")
                        .retrieve()
                        .body(new ParameterizedTypeReference <Map<String, Double>>() {});
            } catch (Exception e) {
                log.error("Erreur récupération productions : {}",
                        e.getMessage());
                return Map.of();
            }
        }

        // Surface cultivée par région
        public Double getSurfaceCultiveeRegion(Integer idRegion) {
            try {
                return restClient.get()
                        .uri("/api/culture/productions" +
                                        "/region/{id}/surface-cultivee",
                                idRegion)
                        .retrieve()
                        .body(Double.class);
            } catch (Exception e) {
                log.error("Erreur surface cultivée région {} : {}",
                        idRegion, e.getMessage());
                return 0.0;
            }
        }
    }

