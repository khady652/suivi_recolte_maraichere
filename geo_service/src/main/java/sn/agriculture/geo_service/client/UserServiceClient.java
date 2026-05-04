package sn.agriculture.geo_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

    @Service
    @Slf4j
    public class UserServiceClient {

        private final RestClient restClient;

        public UserServiceClient(
                RestClient.Builder builder,
                @Value("${users.service.base-url}") String usersBaseUrl) {
            this.restClient = builder.baseUrl(usersBaseUrl).build();
        }

        public String[] getNomPrenomDirecteurSDDR(Integer idDirecteur) {
            try {
                var response = restClient.get()
                        .uri("/api/users/directeurs/sddr/" + idDirecteur + "/info")
                        .retrieve()
                        .toEntity(Map.class);
                String nom = (String) response.getBody().get("nom");
                String prenom = (String) response.getBody().get("prenom");
                return new String[]{nom, prenom};
            } catch (Exception e) {
                log.error("Erreur récupération directeur SDDR : {}",
                        e.getMessage());
                return new String[]{null, null};
            }
        }

        public String[] getNomPrenomDirecteurDR(Integer idDirecteur) {
            try {
                var response = restClient.get()
                        .uri("/api/users/directeurs/dr/" + idDirecteur + "/info")
                        .retrieve()
                        .toEntity(Map.class);
                String nom = (String) response.getBody().get("nom");
                String prenom = (String) response.getBody().get("prenom");
                return new String[]{nom, prenom};
            } catch (Exception e) {
                log.error("Erreur récupération directeur DR : {}",
                        e.getMessage());
                return new String[]{null, null};
            }
        }
        public Integer getIdServiceRegionalByDirecteurDR(Integer userId) {
            try {
                var response = restClient.get()
                        .uri("/api/users/directeurs/dr/" + userId + "/info")
                        .retrieve()
                        .toEntity(Map.class);
                return (Integer) response.getBody()
                        .get("idServiceRegional");
            } catch (Exception e) {
                log.error("Erreur récupération directeur DR : {}",
                        e.getMessage());
                return null;
            }
        }
    }
