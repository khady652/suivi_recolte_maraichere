package sn.user_service.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.user_service.dto.Requests.AgriculteurRequest;
import sn.user_service.dto.Responses.AgriculteurReponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.entity.Agriculteur;
import sn.user_service.entity.ChefCooperatif;
import sn.user_service.entity.Cooperative;
import sn.user_service.entity.DirecteurSDDR;
import sn.user_service.exception.UserException;
import sn.user_service.repository.AgriculteurRepo;
import sn.user_service.repository.ChefCooperatifRepo;
import sn.user_service.repository.CooperativeRepository;
import sn.user_service.repository.DirecteurSddrRepo;

import java.util.List;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class AgiculteurService {

        private final AgriculteurRepo agriculteurRepository;
        private final CooperativeRepository cooperativeRepository;
        private final DirecteurSddrRepo directeurSddrRepo;
        private final ChefCooperatifRepo chefCooperatifRepo;

        // ── CRÉER ─────────────────────────────────────────────
        @Transactional
        public MessageResponse creer(AgriculteurRequest request) {

            // Validation
            if (request.getNom() == null || request.getNom().isEmpty())
                throw new UserException("Le nom est obligatoire");
            if (request.getPrenom() == null || request.getPrenom().isEmpty())
                throw new UserException("Le prénom est obligatoire");

            // Créer l'agriculteur
            Agriculteur agriculteur = new Agriculteur();
            agriculteur.setNom(request.getNom());
            agriculteur.setPrenom(request.getPrenom());
            agriculteur.setAdresse(request.getAdresse());
            agriculteur.setEmail(request.getEmail());
            agriculteur.setTelephone(request.getTelephone());
            agriculteur.setAnneeExperience(request.getAnneeExperience());
            agriculteur.setNiveauInstruction(request.getNiveauInstruction());
            agriculteur.setRole("AGRICULTEUR");
            agriculteur.setActif(false);

            // Coopérative optionnelle
            if (request.getIdCooperative() != null) {
                Cooperative coop = cooperativeRepository
                        .findById(request.getIdCooperative())
                        .orElseThrow(() ->
                                new UserException("Coopérative introuvable"));
                agriculteur.setCooperative(coop);
            }

            agriculteurRepository.save(agriculteur);
            log.info("Agriculteur créé : {} {}",
                    request.getNom(), request.getPrenom());

            return new MessageResponse(
                    "Agriculteur " + request.getNom() + " créé avec succès",
                    true);
        }

        // ── LIRE TOUS ─────────────────────────────────────────
        @Transactional
        public List<AgriculteurReponse> getAll() {
            return agriculteurRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── LIRE UN
        @Transactional
        public AgriculteurReponse getById(Integer id) {
            Agriculteur agriculteur = agriculteurRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Agriculteur introuvable"));
            return toResponse(agriculteur);
        }

        // ── MODIFIER ──────────────────────────────────────────
        @Transactional
        public MessageResponse update(
                Integer id, AgriculteurRequest request) {

            Agriculteur agriculteur = agriculteurRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Agriculteur introuvable"));

            if (request.getNom() != null)
                agriculteur.setNom(request.getNom());
            if (request.getPrenom() != null)
                agriculteur.setPrenom(request.getPrenom());
            if (request.getAdresse() != null)
                agriculteur.setAdresse(request.getAdresse());
            if (request.getTelephone() != null)
                agriculteur.setTelephone(request.getTelephone());
            if (request.getAnneeExperience() != null)
                agriculteur.setAnneeExperience(request.getAnneeExperience());
            if (request.getNiveauInstruction() != null)
                agriculteur.setNiveauInstruction(
                        request.getNiveauInstruction());
            if (request.getIdCooperative() != null) {
                Cooperative coop = cooperativeRepository
                        .findById(request.getIdCooperative())
                        .orElseThrow(() ->
                                new UserException("Coopérative introuvable"));
                agriculteur.setCooperative(coop);
            }

            agriculteurRepository.save(agriculteur);
            return new MessageResponse(
                    "Agriculteur modifié avec succès", true);
        }

        // ── SUPPRIMER ─────────────────────────────────────────
        @Transactional
        public MessageResponse delete(Integer id) {
            Agriculteur agriculteur = agriculteurRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Agriculteur introuvable"));
            agriculteurRepository.delete(agriculteur);
            return new MessageResponse(
                    "Agriculteur supprimé avec succès", true);
        }

        // ── MÉTHODE UTILITAIRE ────────────────────────────────
        private AgriculteurReponse toResponse(Agriculteur a) {
            AgriculteurReponse response = new AgriculteurReponse();
            response.setIdUtilisateur(a.getIdUtilisateur());
            response.setNom(a.getNom());
            response.setPrenom(a.getPrenom());
            response.setAdresse(a.getAdresse());
            response.setEmail(a.getEmail());
            response.setTelephone(a.getTelephone());
            response.setAnneeExperience(a.getAnneeExperience());
            response.setNiveauInstruction(a.getNiveauInstruction());
            response.setActif(a.getActif());
            if (a.getCooperative() != null) {
                response.setNomCooperative(
                        a.getCooperative().getNomCooperative());
            }
            return response;
        }
        // ── MON PROFIL ────────────────────────────────────────
        public AgriculteurReponse getMonProfil(Integer userId) {
            Agriculteur agriculteur = agriculteurRepository
                    .findById(userId)
                    .orElseThrow(() ->
                            new UserException("Profil introuvable"));
            return toResponse(agriculteur);
        }

        // pour voir les  agriculteurs
        public List<AgriculteurReponse> getAgriculteursByRole(
                Integer userId, String role) {

            switch (role) {
// pour voir les  agriculteurs dans une cooperation agricole
                case "CHEF_COOPERATIF" -> {
                    // Direct — lien direct avec Cooperative
                    ChefCooperatif chef = chefCooperatifRepo
                            .findById(userId)
                            .orElseThrow(() ->
                                    new UserException("Chef introuvable"));
                    return getByCooperative(
                            chef.getCooperative().getIdCooperation());
                }
// pour voir les  agriculteurs au niveau departementale
                case "DIRECTEUR_SDDR" -> {
                    // Via geo-service → chercher cooperatives du département
                    // Pour l'instant retourner tous ← temporaire
                    return getAll();
                }
// pour voir les  agriculteurs au niveau regionale
                case "DIRECTEUR_DR" -> {
                    // Via geo-service → chercher cooperatives de la région
                    // Pour l'instant retourner tous ← temporaire
                    return getAll();
                }

                default -> throw new UserException("Accès non autorisé");
            }
        }
        public List<AgriculteurReponse> getByCooperative(
                Integer idCooperative) {
            return agriculteurRepository
                    .findByCooperativeIdCooperation(idCooperative)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }
        }


