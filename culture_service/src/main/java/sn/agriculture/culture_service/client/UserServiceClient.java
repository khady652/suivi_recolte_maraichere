package sn.agriculture.culture_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import sn.agriculture.culture_service.util.AgriculteurResponse;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(@Value("${user.service.base-url}") String userServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    // Récupérer un agriculteur par ID
    // GET /api/users/agriculteurs/{id}
    public AgriculteurResponse getAgriculteurById(Integer id) {
        return restClient.get()
                .uri("/api/users/agriculteurs/{id}", id)
                .retrieve()
                .body(AgriculteurResponse.class);
    }

    // Récupérer les agriculteurs d'un chef coopératif
    // GET /api/users/chefs-cooperatifs/mes-agriculteurs
    /*public List<AgriculteurResponse> getMesAgriculteurs(String token) {
        return restClient.get()
                .uri("/api/users/chefs-cooperatifs/mes-agriculteurs")
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AgriculteurResponse>>() {});
    }*/
    public List<AgriculteurResponse> getMesAgriculteurs(
            Integer chefId) {
        try {
            return restClient.get()
                    .uri("/api/users/chefs-cooperatifs/mes-agriculteurs")
                    .header("X-User-Id", chefId.toString())
                    .header("X-User-Role", "CHEF_COOPERATIF")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("Erreur récupération agriculteurs chef {} : {}",
                    chefId, e.getMessage());
            return List.of();
        }
    }
    // Récupérer les agriculteurs par rôle SDDR ou DRDR
    // GET /api/users/agriculteurs/mes-agriculteurs
    public List<AgriculteurResponse> getAgriculteursByRole(String token) {
        return restClient.get()
                .uri("/api/users/agriculteurs/mes-agriculteurs")
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AgriculteurResponse>>() {});
    }

    // Récupérer les infos du SDDR (idDepartement)
    // GET /api/users/directeurs/sddr/{id}/info
    public Map<String, Object> getSDDRInfo(Integer id) {
        try {
            return restClient.get()
                    .uri("/api/users/directeurs/sddr/{id}/info", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("Erreur récupération SDDR {} : {}", id, e.getMessage());
            return null;
        }
    }

    // Récupérer les infos du DRDR (idRegion)
    // GET /api/users/directeurs/dr/{id}/info
    public Map<String, Object> getDRInfo(Integer id) {
        return restClient.get()
                .uri("/api/users/directeurs/dr/{id}/info", id)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public Map<String, String> getAgriculteurInfo(Integer id) {
        try {
            return restClient.get()
                    .uri("/api/users/agriculteurs/{id}/info", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("Erreur récupération agriculteur {} : {}",
                    id, e.getMessage());
            return null;
        }
    }

    public Map<String, String> getChefCooperatifByAgriculteur(
            Integer idAgriculteur) {
        try {
            return restClient.get()
                    .uri("/api/users/agriculteurs/{id}/chef-info",
                            idAgriculteur)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("Erreur récupération chef : {}",
                    e.getMessage());
            return null;
        }
    }
    public List<Map<String, String>> getAllDecideurs() {
        try {
            return restClient.get()
                    .uri("/api/users/decideurs")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            log.error("Erreur récupération décideurs : {}",
                    e.getMessage());
            return List.of();
        }
    }
}