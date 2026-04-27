package sn.user_service.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.user_service.dto.Requests.DecideurRequest;
import sn.user_service.dto.Responses.DecideurResponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.entity.DecideurARM;
import sn.user_service.exception.UserException;
import sn.user_service.repository.DecideurRepo;
import sn.user_service.repository.UtilisateurRepository;

import java.util.List;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class DecideurService {

        private final DecideurRepo decideurARMRepository;
        private final UtilisateurRepository utilisateurRepository;

        // ── CRÉER ─────────────────────────────────────────────
        @Transactional
        public MessageResponse creer(DecideurRequest request) {

            if (request.getNom() == null || request.getNom().isEmpty())
                throw new UserException("Le nom est obligatoire");

            if (request.getEmail() != null &&
                    utilisateurRepository.existsByEmail(request.getEmail()))
                throw new UserException("Cet email est déjà utilisé");

            DecideurARM decideur = new DecideurARM();
            decideur.setNom(request.getNom());
            decideur.setPrenom(request.getPrenom());
            decideur.setAdresse(request.getAdresse());
            decideur.setEmail(request.getEmail());
            decideur.setTelephone(request.getTelephone());
            decideur.setRole("DECIDEUR_ARM");
            decideur.setActif(false);

            decideurARMRepository.save(decideur);
            log.info("Décideur ARM créé : {}", request.getNom());

            return new MessageResponse(
                    "Décideur ARM " + request.getNom() +
                            " créé avec succès", true);
        }

        // ── LIRE TOUS ─────────────────────────────────────────
        public List<DecideurResponse> getAll() {
            return decideurARMRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── LIRE UN ───────────────────────────────────────────
        public DecideurResponse getById(Integer id) {
            DecideurARM decideur = decideurARMRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Décideur ARM introuvable"));
            return toResponse(decideur);
        }

        // ── MODIFIER ──────────────────────────────────────────
        @Transactional
        public MessageResponse update(
                Integer id, DecideurRequest request) {

            DecideurARM decideur = decideurARMRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Décideur ARM introuvable"));

            if (request.getNom() != null)
                decideur.setNom(request.getNom());
            if (request.getPrenom() != null)
                decideur.setPrenom(request.getPrenom());
            if (request.getAdresse() != null)
                decideur.setAdresse(request.getAdresse());
            if (request.getTelephone() != null)
                decideur.setTelephone(request.getTelephone());

            decideurARMRepository.save(decideur);
            return new MessageResponse(
                    "Décideur ARM modifié avec succès", true);
        }

        // ── SUPPRIMER ─────────────────────────────────────────
        @Transactional
        public MessageResponse delete(Integer id) {
            DecideurARM decideur = decideurARMRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Décideur ARM introuvable"));
            decideurARMRepository.delete(decideur);
            return new MessageResponse(
                    "Décideur ARM supprimé avec succès", true);
        }

        // ── MÉTHODE UTILITAIRE ────────────────────────────────
        private DecideurResponse toResponse(DecideurARM d) {
            DecideurResponse response = new DecideurResponse();
            response.setIdUtilisateur(d.getIdUtilisateur());
            response.setNom(d.getNom());
            response.setPrenom(d.getPrenom());
            response.setEmail(d.getEmail());
            response.setTelephone(d.getTelephone());
            response.setActif(d.getActif());
            return response;
        }
        public DecideurResponse getMonProfil(Integer userId) {
            DecideurARM decideur = decideurARMRepository
                    .findById(userId)
                    .orElseThrow(() ->
                            new UserException("Profil introuvable"));
            return toResponse(decideur);
        }
    }

