package sn.user_service.service;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.user_service.dto.Requests.ChefCooperatifRequest;
import sn.user_service.dto.Responses.ChefCooperatifResponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.entity.ChefCooperatif;
import sn.user_service.entity.Cooperative;
import sn.user_service.exception.UserException;
import sn.user_service.repository.ChefCooperatifRepo;
import sn.user_service.repository.CooperativeRepository;
import sn.user_service.repository.UtilisateurRepository;

import java.util.List;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class ChefCooperativeService {

        private final ChefCooperatifRepo chefCooperatifRepository;
        private final CooperativeRepository cooperativeRepository;
        private final UtilisateurRepository utilisateurRepository;

        // ── CRÉER ─────────────────────────────────────────────
        @Transactional
        public MessageResponse creer(ChefCooperatifRequest request) {

            if (request.getNom() == null || request.getNom().isEmpty())
                throw new UserException("Le nom est obligatoire");

            if (request.getEmail() != null &&
                    utilisateurRepository.existsByEmail(request.getEmail()))
                throw new UserException("Cet email est déjà utilisé");

            // Vérifier que la coopérative existe
            Cooperative cooperative = cooperativeRepository
                    .findById(request.getIdCooperative())
                    .orElseThrow(() ->
                            new UserException("Coopérative introuvable"));

            ChefCooperatif chef = new ChefCooperatif();
            chef.setNom(request.getNom());
            chef.setPrenom(request.getPrenom());
            chef.setAdresse(request.getAdresse());
            chef.setEmail(request.getEmail());
            chef.setTelephone(request.getTelephone());
            chef.setCooperative(cooperative);
            chef.setRole("CHEF_COOPERATIF");
            chef.setActif(false);

            chefCooperatifRepository.save(chef);
            log.info("Chef coopératif créé : {}", request.getNom());

            return new MessageResponse(
                    "Chef coopératif " + request.getNom() +
                            " créé avec succès", true);
        }

        // ── LIRE TOUS ─────────────────────────────────────────
        public List<ChefCooperatifResponse> getAll() {
            return chefCooperatifRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── LIRE UN ───────────────────────────────────────────
        public ChefCooperatifResponse getById(Integer id) {
            ChefCooperatif chef = chefCooperatifRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Chef coopératif introuvable"));
            return toResponse(chef);
        }

        // ── MODIFIER ──────────────────────────────────────────
        @Transactional
        public MessageResponse update(
                Integer id, ChefCooperatifRequest request) {

            ChefCooperatif chef = chefCooperatifRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Chef coopératif introuvable"));

            if (request.getNom() != null)
                chef.setNom(request.getNom());
            if (request.getPrenom() != null)
                chef.setPrenom(request.getPrenom());
            if (request.getAdresse() != null)
                chef.setAdresse(request.getAdresse());
            if (request.getTelephone() != null)
                chef.setTelephone(request.getTelephone());
            if (request.getIdCooperative() != null) {
                Cooperative cooperative = cooperativeRepository
                        .findById(request.getIdCooperative())
                        .orElseThrow(() ->
                                new UserException("Coopérative introuvable"));
                chef.setCooperative(cooperative);
            }

            chefCooperatifRepository.save(chef);
            return new MessageResponse(
                    "Chef coopératif modifié avec succès", true);
        }

        // ── SUPPRIMER ─────────────────────────────────────────
        @Transactional
        public MessageResponse delete(Integer id) {
            ChefCooperatif chef = chefCooperatifRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Chef coopératif introuvable"));
            chefCooperatifRepository.delete(chef);
            return new MessageResponse(
                    "Chef coopératif supprimé avec succès", true);
        }

        // ── MÉTHODE UTILITAIRE ────────────────────────────────
        private ChefCooperatifResponse toResponse(ChefCooperatif c) {
            ChefCooperatifResponse response = new ChefCooperatifResponse();
            response.setIdUtilisateur(c.getIdUtilisateur());
            response.setNom(c.getNom());
            response.setPrenom(c.getPrenom());
            response.setEmail(c.getEmail());
            response.setTelephone(c.getTelephone());
            response.setActif(c.getActif());
            if (c.getCooperative() != null) {
                response.setNomCooperative(
                        c.getCooperative().getNomCooperative());
            }
            return response;
        }
        public ChefCooperatifResponse getMonProfil(Integer userId) {
            ChefCooperatif chef = chefCooperatifRepository
                    .findById(userId)
                    .orElseThrow(() ->
                            new UserException("Profil introuvable"));
            return toResponse(chef);
        }
    }

