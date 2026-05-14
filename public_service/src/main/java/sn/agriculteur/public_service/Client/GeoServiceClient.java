package sn.agriculteur.public_service.Client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import sn.agriculteur.public_service.Response.RegionResponse;

import java.util.List;

    @Component
    @Slf4j
    public class GeoServiceClient {

        private final RestClient restClient;

        public GeoServiceClient(
                @Value("${geo.service.base-url}") String baseUrl) {
            this.restClient = RestClient.builder()
                    .baseUrl(baseUrl)
                    .build();
        }

        public List<RegionResponse> getAllRegions() {
            try {
                return restClient.get()
                        .uri("/api/geo/regions")
                        .retrieve()
                        .body(new ParameterizedTypeReference <List<RegionResponse>>() {});
            } catch (Exception e) {
                log.error("Erreur récupération régions : {}",
                        e.getMessage());
                return List.of();
            }
        }
    }

