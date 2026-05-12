package sn.agriculture.geo_service.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
    @Slf4j
    public class CultureServiceClient {

        private final RestClient restClient;

        public CultureServiceClient(
                @Value("${culture.service.base-url}") String cultureServiceUrl) {
            this.restClient = RestClient.builder()
                    .baseUrl(cultureServiceUrl)
                    .build();
        }

        // Surface cultivée année en cours par département
        public Double getSurfaceCultiveeDepartement(
                Long idDepartement, int annee) {
            try {
                return restClient.get()
                        .uri("/api/culture/productions" +
                                        "/departement/{id}/surface-cultivee" +
                                        "?annee={annee}",
                                idDepartement, annee)
                        .retrieve()
                        .body(Double.class);
            } catch (Exception e) {
                log.warn("Surface cultivée indisponible " +
                                "pour département {} : {}",
                        idDepartement, e.getMessage());
                return null;
            }
        }

        // Surface cultivée année en cours par région
        public Double getSurfaceCultiveeRegion(
                Integer idRegion, int annee) {
            try {
                return restClient.get()
                        .uri("/api/culture/productions" +
                                        "/region/{id}/surface-cultivee" +
                                        "?annee={annee}",
                                idRegion, annee)
                        .retrieve()
                        .body(Double.class);
            } catch (Exception e) {
                log.warn("Surface cultivée indisponible " +
                                "pour région {} : {}",
                        idRegion, e.getMessage());
                return null;
            }
        }
    }

