package sn.user_service.Client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

    @Service
    @Slf4j
    public class GeoServiceClient {

        private final RestClient restClient;


        // @Value ici dans le constructeur
        public GeoServiceClient(
                RestClient.Builder builder,
                @Value("${geo.service.base-url}") String geoBaseUrl) {
            this.restClient = builder.baseUrl(geoBaseUrl).build();
        }

        // ✅ Affecter directeur DR à un service régional
        public void affecterDirecteurDR(
                Integer idServiceRegional, Integer idDirecteurDr) {
            try {

                restClient.patch()
                        .uri("/api/geo/services-regionaux/" +
                                idServiceRegional + "/affecter-directeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("idDirecteurDr", idDirecteurDr))
                        .retrieve()
                        .toBodilessEntity();
            } catch (Exception e) {
                log.error("Erreur affectation directeur DR : {}",
                        e.getMessage());
                throw new RuntimeException(
                        "Échec affectation directeur DR", e);
            }
        }

        // ✅ Affecter directeur SDDR à un service départemental
        public void affecterDirecteurSDDR(
                Integer idServiceDepartementale, Integer idDirecteurSddr) {
            try {
                log.info("Appel geo-service pour affecter directeur SDDR {} au service {}",
                        idDirecteurSddr, idServiceDepartementale); // ✅ Ajout

                restClient.patch()
                        .uri("/api/geo/services-departementaux/" +
                                idServiceDepartementale + "/affecter-directeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("idDirecteurSddr", idDirecteurSddr))
                        .retrieve()
                        .toBodilessEntity();
            } catch (Exception e) {
                log.error("Erreur affectation directeur SDDR : {}",
                        e.getMessage());
                throw new RuntimeException(
                        "Échec affectation directeur SDDR", e);
            }
        }
        public String getNomServiceRegional(Integer idService) {
            try {
                var response = restClient.get()
                        .uri("/api/geo/services-regionaux/" + idService)
                        .retrieve()
                        .toEntity(Map.class);
                return (String) response.getBody().get("nomService");
            } catch (Exception e) {
                log.error("Erreur récupération service régional : {}",
                        e.getMessage());
                return null;
            }
        }

        public String getNomServiceDepartementale(Integer idService) {
            try {
                var response = restClient.get()
                        .uri("/api/geo/services-departementaux/" + idService)
                        .retrieve()
                        .toEntity(Map.class);
                return (String) response.getBody().get("nomService");
            } catch (Exception e) {
                log.error("Erreur récupération service départemental : {}",
                        e.getMessage());
                return null;
            }
        }
    }

