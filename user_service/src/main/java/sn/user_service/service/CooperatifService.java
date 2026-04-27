package sn.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.user_service.dto.Requests.CooperatifRequest;
import sn.user_service.dto.Responses.CooperativeReponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.entity.Cooperative;
import sn.user_service.exception.UserException;
import sn.user_service.repository.CooperativeRepository;

import java.util.List;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class CooperatifService {

        private final CooperativeRepository cooperativeRepository;

        // ── CRÉER ─────────────────────────────────────────────
        @Transactional
        public MessageResponse creer(CooperatifRequest request) {

            if (cooperativeRepository.existsByNomCooperative(
                    request.getNomCooperative())) {
                throw new UserException("Cette coopérative existe déjà");
            }

            Cooperative cooperative = new Cooperative();
            cooperative.setNomCooperative(request.getNomCooperative());
            cooperative.setAdresse(request.getAdresse());
            cooperative.setNombreMembres(request.getNombreMembres());
             cooperative.setDateCreation(request.getDateCreation());
            cooperativeRepository.save(cooperative);
            log.info("Coopérative créée : {}", request.getNomCooperative());

            return new MessageResponse(
                    "Coopérative " + request.getNomCooperative() +
                            " créée avec succès", true);
        }

        // ── LIRE TOUTES ───────────────────────────────────────
        public List<CooperativeReponse> getAll() {
            return cooperativeRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── LIRE UNE ──────────────────────────────────────────
        public CooperativeReponse getById(Integer id) {
            Cooperative cooperative = cooperativeRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Coopérative introuvable"));
            return toResponse(cooperative);
        }

        // ── MODIFIER ──────────────────────────────────────────
        @Transactional
        public MessageResponse update(
                Integer id, CooperatifRequest request) {

            Cooperative cooperative = cooperativeRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Coopérative introuvable"));

            if (request.getNomCooperative() != null)
                cooperative.setNomCooperative(request.getNomCooperative());
            if (request.getAdresse() != null)
                cooperative.setAdresse(request.getAdresse());
            if (request.getNombreMembres() != null)
                cooperative.setNombreMembres(request.getNombreMembres());
            if (request.getDateCreation() != null)
                cooperative.setDateCreation(request.getDateCreation());
            cooperativeRepository.save(cooperative);
            return new MessageResponse(
                    "Coopérative modifiée avec succès", true);
        }

        // ── SUPPRIMER ─────────────────────────────────────────
        @Transactional
        public MessageResponse delete(Integer id) {
            Cooperative cooperative = cooperativeRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new UserException("Coopérative introuvable"));
            cooperativeRepository.delete(cooperative);
            return new MessageResponse(
                    "Coopérative supprimée avec succès", true);
        }

        // ── MÉTHODE UTILITAIRE ────────────────────────────────
        private CooperativeReponse toResponse(Cooperative c) {
            CooperativeReponse response = new CooperativeReponse();
            response.setIdCooperation(c.getIdCooperation());
            response.setNomCooperative(c.getNomCooperative());
            response.setAdresse(c.getAdresse());
            response.setNombreMembres(c.getNombreMembres());
            if (c.getChefCooperatif() != null) {
                response.setNomChef(c.getChefCooperatif().getNom());
            }
            return response;
        }
    }

