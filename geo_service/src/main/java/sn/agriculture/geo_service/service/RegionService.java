package sn.agriculture.geo_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.geo_service.entity.Region;
import sn.agriculture.geo_service.exception.GeoException;

import java.util.List;
import java.util.stream.Collectors;

import sn.agriculture.geo_service.dtos.requests.RegionRequest;
import sn.agriculture.geo_service.dtos.response.MessageResponse;
import sn.agriculture.geo_service.dtos.response.RegionResponse;
import sn.agriculture.geo_service.repository.RegionRepos;

@Service
    @RequiredArgsConstructor
    @Slf4j
    public class RegionService {

        private final RegionRepos regionRepository;

        // ── CRÉER ─────────────────────────────────────────────
        @Transactional
        public MessageResponse creer(RegionRequest request) {

            if (regionRepository.existsByNomRegion(request.getNomRegion()))
                throw new GeoException("Cette région existe déjà");

            Region region = new Region();
            region.setNomRegion(request.getNomRegion());
            region.setPopulation(request.getPopulation());
            region.setSuperficie(request.getSuperficie());

            regionRepository.save(region);
            log.info("Région créée : {}", request.getNomRegion());

            return new MessageResponse(
                    "Région " + request.getNomRegion() +
                            " créée avec succès", true);
        }

        // ── LIRE TOUS ─────────────────────────────────────────
        public List<RegionResponse> getAll() {
            return regionRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── LIRE UNE ──────────────────────────────────────────
        public RegionResponse getById(Integer id) {
            Region region = regionRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new GeoException("Région introuvable"));
            return toResponse(region);
        }

        // ── MODIFIER ──────────────────────────────────────────
        @Transactional
        public MessageResponse update(Integer id, RegionRequest request) {

            Region region = regionRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new GeoException("Région introuvable"));

            if (request.getNomRegion() != null)
                region.setNomRegion(request.getNomRegion());
            if (request.getPopulation() != null)
                region.setPopulation(request.getPopulation());
            if (request.getSuperficie() != null)
                region.setSuperficie(request.getSuperficie());

            regionRepository.save(region);
            return new MessageResponse("Région modifiée avec succès", true);
        }

        // ── SUPPRIMER ─────────────────────────────────────────
        @Transactional
        public MessageResponse delete(Integer id) {
            Region region = regionRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new GeoException("Région introuvable"));
            regionRepository.delete(region);
            return new MessageResponse("Région supprimée avec succès", true);
        }

        // ── MÉTHODE UTILITAIRE ────────────────────────────────
        private RegionResponse toResponse(Region r) {
            RegionResponse response = new RegionResponse();
            response.setIdRegion(r.getIdRegion());
            response.setNomRegion(r.getNomRegion());
            response.setPopulation(r.getPopulation());
            response.setSuperficie(r.getSuperficie());
            response.setSurfaceCultivee(r.getSurfaceCultivee());
            if (r.getServiceRegionale() != null) {
                response.setNomServiceRegionale(
                        r.getServiceRegionale().getNomService());
            }
            return response;
        }
    }



