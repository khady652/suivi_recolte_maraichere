package sn.user_service.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.user_service.Client.AuthServiceClient;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.dto.Responses.UtilisateurResponse;
import sn.user_service.entity.*;
import sn.user_service.exception.UserException;
import sn.user_service.repository.UtilisateurRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class UtilisateurService {

        private final UtilisateurRepository utilisateurRepository;
        private final AuthServiceClient authServiceClient;

        // ── ACTIVER COMPTE ────────────────────────────────────
        @Transactional
        public MessageResponse activerCompte(Integer userId) {
            Utilisateur utilisateur = utilisateurRepository
                    .findById(userId)
                    .orElseThrow(() ->
                            new UserException("Utilisateur introuvable"));

            utilisateur.setActif(true);
            utilisateurRepository.save(utilisateur);

            // Activer aussi dans auth-service
            authServiceClient.activerCompte(userId);

            log.info("Compte activé : {}", userId);
            return new MessageResponse("Compte activé avec succès", true);
        }

        // ── DÉSACTIVER COMPTE ─────────────────────────────────
        @Transactional
        public MessageResponse desactiverCompte(Integer userId) {
            Utilisateur utilisateur = utilisateurRepository
                    .findById(userId)
                    .orElseThrow(() ->
                            new UserException("Utilisateur introuvable"));

            utilisateur.setActif(false);
            utilisateurRepository.save(utilisateur);

            // Désactiver aussi dans auth-service
            authServiceClient.desactiverCompte(userId);

            log.info("Compte désactivé : {}", userId);
            return new MessageResponse("Compte désactivé avec succès", true);
        }

        // ── LISTER TOUS LES UTILISATEURS ─────────────────────
        public List<UtilisateurResponse> getAll() {
            return utilisateurRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── MÉTHODE UTILITAIRE ────────────────────────────────
        /*private UtilisateurResponse toResponse(Utilisateur u) {
            UtilisateurResponse response = new UtilisateurResponse();
            response.setIdUtilisateur(u.getIdUtilisateur());
            response.setNom(u.getNom());
            response.setPrenom(u.getPrenom());
            response.setEmail(u.getEmail());
            response.setTelephone(u.getTelephone());
            response.setRole(u.getRole());
            response.setActif(u.getActif());
            response.setAdresse(u.getAdresse());
            return response;
        }*/
        private UtilisateurResponse toResponse(Utilisateur u) {
            UtilisateurResponse response = new UtilisateurResponse();
            response.setIdUtilisateur(u.getIdUtilisateur());
            response.setNom(u.getNom());
            response.setPrenom(u.getPrenom());
            response.setEmail(u.getEmail());
            response.setTelephone(u.getTelephone());
            response.setRole(u.getRole());
            response.setActif(u.getActif());
            response.setAdresse(u.getAdresse());

            // ✅ Champs spécifiques selon le rôle
            if (u instanceof Agriculteur a) {
                response.setAnneeExperience(a.getAnneeExperience());
                response.setNiveauInstruction(a.getNiveauInstruction());
                if (a.getCooperative() != null)
                    response.setNomCooperative(a.getCooperative().getNomCooperative());
            }
            if (u instanceof ChefCooperatif c) {
                if (c.getCooperative() != null) {
                    response.setNomCooperative(c.getCooperative().getNomCooperative());
                    response.setIdCooperative(c.getCooperative().getIdCooperation());
                }
            }
            if (u instanceof EnqueteurMarche e) {
                response.setOrganisation(e.getOrganisation());
                response.setZoneAffectation(e.getZoneAffectation());
            }
            if (u instanceof DirecteurSDDR d) {
                response.setSpecialite(d.getSpecialite());
            }
            if (u instanceof DirecteurDRDR d) {
                response.setSpecialite(d.getSpecialite());
            }
            if (u.getCreatedAt() != null) {
                response.setDateCreation(
                        u.getCreatedAt().format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )
                );
            }
            return response;
        }
        // ── MON PROFIL ────────────────────────────────────────
        public UtilisateurResponse getMonProfil(Integer userId) {
            Utilisateur utilisateur = utilisateurRepository
                    .findById(userId)
                    .orElseThrow(() ->
                            new UserException("Profil introuvable"));
            return toResponse(utilisateur);
        }
    }

