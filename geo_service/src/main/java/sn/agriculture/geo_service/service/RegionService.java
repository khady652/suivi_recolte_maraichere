package sn.agriculture.geo_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.geo_service.client.CultureServiceClient;
import sn.agriculture.geo_service.client.UserServiceClient;
import sn.agriculture.geo_service.entity.Region;
import sn.agriculture.geo_service.exception.GeoException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import sn.agriculture.geo_service.dtos.requests.RegionRequest;
import sn.agriculture.geo_service.dtos.response.MessageResponse;
import sn.agriculture.geo_service.dtos.response.RegionResponse;
import sn.agriculture.geo_service.repository.RegionRepos;
import sn.agriculture.geo_service.repository.ServiceRegRepos;

@Service
    @RequiredArgsConstructor
    @Slf4j
public class RegionService {

    private final RegionRepos regionRepository;
    private final CultureServiceClient cultureServiceClient;
    private final ServiceRegRepos serviceRegRepos;
    private final UserServiceClient userServiceClient;

    // ── CRÉER ─────────────────────────────────────────────
    @Transactional
    public MessageResponse creer(RegionRequest request) {
        if (regionRepository.existsByNomRegion(request.getNomRegion()))
            throw new GeoException("Cette région existe déjà");

        Region region = new Region();
        region.setNomRegion(request.getNomRegion());
        region.setPopulation(request.getPopulation());
        region.setSuperficie(request.getSuperficie());
        region.setLatitude(request.getLatitude());
        region.setLongitude(request.getLongitude());
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
                .map(r -> toResponse(r, false)) // ← sans surface
                .collect(Collectors.toList());
    }

    // ── LIRE UNE ──────────────────────────────────────────
    public RegionResponse getById(Integer id) {
        Region region = regionRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Région introuvable"));
        return toResponse(region, false); // ← sans surface
    }

    // ── MA RÉGION (Directeur DR) ───────────────────────────
    public RegionResponse getMaRegion(Integer userId) {

        // 1. Récupérer idServiceRegional du directeur DR
        Map<String, Object> drInfo = userServiceClient
                .getDRInfo(userId);
        if (drInfo == null)
            throw new GeoException("Directeur DR introuvable !");

        Integer idServiceReg = (Integer) drInfo
                .get("idServiceRegional");
        if (idServiceReg == null)
            throw new GeoException(
                    "Directeur DR sans service régional !");

        // 2. Récupérer la région via le service
        var service = serviceRegRepos
                .findById(idServiceReg)
                .orElseThrow(() -> new GeoException(
                        "Service régional introuvable !"));

        // 3. Retourner AVEC surfaceCultivee ✅
        return toResponse(service.getRegion(), true);
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
        return new MessageResponse(
                "Région modifiée avec succès", true);
    }

    // ── SUPPRIMER ─────────────────────────────────────────
    @Transactional
    public MessageResponse delete(Integer id) {
        Region region = regionRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Région introuvable"));
        regionRepository.delete(region);
        return new MessageResponse(
                "Région supprimée avec succès", true);
    }

    // ── MÉTHODE UTILITAIRE ────────────────────────────────
    private RegionResponse toResponse(
            Region r, boolean avecSurface) {

        RegionResponse response = new RegionResponse();
        response.setIdRegion(r.getIdRegion());
        response.setNomRegion(r.getNomRegion());
        response.setPopulation(r.getPopulation());
        response.setSuperficie(r.getSuperficie());
        response.setLatitude(r.getLatitude());
        response.setLongitude(r.getLongitude());
        // ✅ surfaceCultivee SEULEMENT pour Directeur DR
        if (avecSurface) {
            Double surface = cultureServiceClient
                    .getSurfaceCultiveeRegion(
                            r.getIdRegion(),
                            LocalDate.now().getYear());
            response.setSurfaceCultivee(surface);
        } else {
            response.setSurfaceCultivee(null);
        }

        if (r.getServiceRegionale() != null) {
            response.setNomServiceRegionale(
                    r.getServiceRegionale().getNomService());
        }
        return response;
    }
}



