package sn.agriculture.geo_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

        // ── NOM PRÉNOM DIRECTEUR SDDR ─────────────────────────
        public String[] getNomPrenomDirecteurSDDR(Integer idDirecteur) {
            try {
                Map<String, Object> response = restClient.get()
                        .uri("/api/users/directeurs/sddr/{id}/info",
                                idDirecteur)
                        .retrieve()
                        .body(new ParameterizedTypeReference<Map<String, Object>>() {});
                if (response == null)
                    return new String[]{null, null};
                return new String[]{
                        (String) response.get("nom"),
                        (String) response.get("prenom")
                };
            } catch (Exception e) {
                log.error("Erreur récupération directeur SDDR {} : {}",
                        idDirecteur, e.getMessage());
                return new String[]{null, null};
            }
        }

        // ── NOM PRÉNOM DIRECTEUR DR ───────────────────────────
        public String[] getNomPrenomDirecteurDR(Integer idDirecteur) {
            try {
                Map<String, Object> response = restClient.get()
                        .uri("/api/users/directeurs/dr/{id}/info",
                                idDirecteur)
                        .retrieve()
                        .body(new ParameterizedTypeReference<Map<String, Object>>() {});
                if (response == null)
                    return new String[]{null, null};
                return new String[]{
                        (String) response.get("nom"),
                        (String) response.get("prenom")
                };
            } catch (Exception e) {
                log.error("Erreur récupération directeur DR {} : {}",
                        idDirecteur, e.getMessage());
                return new String[]{null, null};
            }
        }

        // ── INFO COMPLÈTE DIRECTEUR SDDR ──────────────────────
        public Map<String, Object> getSDDRInfo(Integer userId) {
            try {
                return restClient.get()
                        .uri("/api/users/directeurs/sddr/{id}/info", userId)
                        .retrieve()
                        .body(new ParameterizedTypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.error("Erreur récupération SDDR {} : {}",
                        userId, e.getMessage());
                return null;
            }
        }

        // ── INFO COMPLÈTE DIRECTEUR DR ────────────────────────
        public Map<String, Object> getDRInfo(Integer userId) {
            try {
                return restClient.get()
                        .uri("/api/users/directeurs/dr/{id}/info", userId)
                        .retrieve()
                        .body(new ParameterizedTypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.error("Erreur récupération DR {} : {}",
                        userId, e.getMessage());
                return null;
            }
        }

        // ── ID SERVICE RÉGIONAL PAR DIRECTEUR DR ──────────────
        public Integer getIdServiceRegionalByDirecteurDR(Integer userId) {
            try {
                Map<String, Object> info = getDRInfo(userId);
                return info != null
                        ? (Integer) info.get("idServiceRegional")
                        : null;
            } catch (Exception e) {
                log.error("Erreur récupération service DR {} : {}",
                        userId, e.getMessage());
                return null;
            }
        }}