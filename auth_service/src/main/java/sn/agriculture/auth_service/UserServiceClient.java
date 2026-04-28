package sn.agriculture.auth_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        private final RestClient.Builder restClientBuilder;

        public void creerProfil(Integer userId, RegisterRequest request) {
            try {
                String url = switch (request.getRole() != null ?
                        request.getRole() : "AGRICULTEUR") {
                    case "ADMINISTRATEUR" ->
                            "http://localhost:3002/api/users/administrateurs";
                    case "DIRECTEUR_DR" ->
                            "http://localhost:3002/api/users/directeurs/dr";
                    case "DIRECTEUR_SDDR" ->
                            "http://localhost:3002/api/users/directeurs/sddr";
                    case "CHEF_COOPERATIF" ->
                            "http://localhost:3002/api/users/chefs-cooperatifs";
                    case "ENQUETEUR_MARCHE" ->
                            "http://localhost:3002/api/users/enqueteurs";
                    case "DECIDEUR_ARM" ->
                            "http://localhost:3002/api/users/decideurs";
                    default ->
                            "http://localhost:3002/api/users/agriculteurs";
                };

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

                restClientBuilder.build()
                        .post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .toBodilessEntity();

                log.info("Profil créé dans user-service : {}", userId);

            } catch (Exception e) {
                log.error("Erreur création profil : {}", e.getMessage());
            }
        }
    }

