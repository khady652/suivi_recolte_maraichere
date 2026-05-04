package sn.user_service.Client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
public class AuthServiceClient {

    private final RestClient restClient;

    public AuthServiceClient(
            RestClient.Builder builder,
            @Value("${auth.service.base-url}") String authBaseUrl) {
        this.restClient = builder.baseUrl(authBaseUrl).build();
    }

    public Integer createAccount(String email,
                                 String telephone,
                                 String role) {
        try {
            Map<String, Object> body = Map.of(
                    "email", email,
                    "telephone", telephone != null ? telephone : "",
                    "password", "Agri@1234",
                    "role", role
            );

            var response = restClient.post()
                    .uri("/api/auth/internal/create-account")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toEntity(Map.class);

            // ✅ Récupérer le userId
            return (Integer) response.getBody().get("userId");

        } catch (Exception e) {
            log.error("Erreur création compte : {}", e.getMessage());
            throw new RuntimeException("Échec création compte", e);
        }
    }
    public void activerCompte(Integer userId) {
        try {
            restClient.patch()
                    .uri("/api/auth/internal/activer/" + userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Erreur activation compte : {}", e.getMessage());
            throw new RuntimeException("Échec activation compte", e);
        }
    }

    public void desactiverCompte(Integer userId) {
        try {
            restClient.patch()
                    .uri("/api/auth/internal/desactiver/" + userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Erreur désactivation compte : {}", e.getMessage());
            throw new RuntimeException("Échec désactivation compte", e);
        }
    }
}