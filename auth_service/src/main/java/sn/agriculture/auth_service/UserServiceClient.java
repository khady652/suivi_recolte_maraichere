package sn.agriculture.auth_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import sn.agriculture.auth_service.dto.AuthDto.RegisterRequest;

import java.util.HashMap;
import java.util.Map;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class UserServiceClient {
        @Value("${users.service.base-url}")
        private String usersServiceBaseUrl;
        private final RestClient.Builder restClientBuilder;

        public void creerProfil(Integer userId, RegisterRequest request) {
            try {
                String url = usersServiceBaseUrl + "/api/users/agriculteurs";

                // body extrait dans une méthode séparée
                Map<String, Object> body = buildAgriculteurBody(userId, request);

                restClientBuilder.build()
                        .post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .toBodilessEntity();

                log.info("Profil agriculteur créé pour userId: {}", userId);

            } catch (Exception e) {
                log.error("Erreur création profil pour userId {}: {}", userId, e.getMessage());
                throw new RuntimeException("Échec création profil agriculteur", e);
            }
        }

        // Méthode
        private Map<String, Object> buildAgriculteurBody(Integer userId, RegisterRequest request) {
            Map<String, Object> body = new HashMap<>();
            body.put("userId", userId);
            body.put("nom", request.getNom());
            body.put("prenom", request.getPrenom());
            body.put("adresse", request.getAdresse());
            body.put("email", request.getEmail());
            body.put("telephone", request.getTelephone());
            body.put("anneeExperience", request.getAnneeExperience());
            body.put("niveauInstruction", request.getNiveauInstruction());
            body.put("idCooperative", request.getIdCooperative());
            return body;
        }
    }

