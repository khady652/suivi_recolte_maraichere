package sn.agriculture.geo_service.service;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.geo_service.client.CultureServiceClient;
import sn.agriculture.geo_service.client.UserServiceClient;
import sn.agriculture.geo_service.dtos.requests.DepartementRequests;
import sn.agriculture.geo_service.dtos.response.DepartementResponse;
import sn.agriculture.geo_service.dtos.response.MessageResponse;
import sn.agriculture.geo_service.entity.Departement;
import sn.agriculture.geo_service.entity.Region;
import sn.agriculture.geo_service.exception.GeoException;
import sn.agriculture.geo_service.repository.DepartementRepos;
import sn.agriculture.geo_service.repository.RegionRepos;
import sn.agriculture.geo_service.repository.ServiceDepRepos;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartementService {

    private final DepartementRepos departementRepository;
    private final RegionRepos regionRepository;
    private final CultureServiceClient cultureServiceClient;
    private final ServiceDepRepos serviceDepRepository;
    private final UserServiceClient userServiceClient;

    // ── CRÉER ─────────────────────────────────────────────
    @Transactional
    public MessageResponse creer(DepartementRequests request) {
        if (departementRepository.existsByNomDepartement(
                request.getNomDepartement()))
            throw new GeoException("Ce département existe déjà");

        Region region = regionRepository
                .findById(request.getIdRegion())
                .orElseThrow(() ->
                        new GeoException("Région introuvable"));

        Departement departement = new Departement();
        departement.setNomDepartement(request.getNomDepartement());
        departement.setPopulation(request.getPopulation());
        departement.setSuperficie(request.getSuperficie());
        departement.setLatitude(request.getLatitude());
        departement.setLongitude(request.getLongitude());
        departement.setRegion(region);

        departementRepository.save(departement);
        log.info("Département créé : {}", request.getNomDepartement());

        return new MessageResponse(
                "Département " + request.getNomDepartement() +
                        " créé avec succès", true);
    }

    // ── LIRE TOUS ─────────────────────────────────────────
    public List<DepartementResponse> getAll() {
        return departementRepository.findAll()
                .stream()
                .map(d -> toResponse(d, false)) // ← sans surface
                .collect(Collectors.toList());
    }

    // ── LIRE PAR RÉGION ───────────────────────────────────
    public List<DepartementResponse> getByRegion(Integer idRegion) {
        return departementRepository
                .findByRegionIdRegion(idRegion)
                .stream()
                .map(d -> toResponse(d, false)) // ← sans surface
                .collect(Collectors.toList());
    }

    // ── LIRE UN ───────────────────────────────────────────
    public DepartementResponse getById(Integer id) {
        Departement departement = departementRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Département introuvable"));
        return toResponse(departement, false); // ← sans surface
    }

    // ── MON DÉPARTEMENT (Directeur SDDR) ──────────────────
    public DepartementResponse getMonDepartement(Integer userId) {

        // 1. Récupérer idServiceDep du directeur SDDR
        Map<String, Object> sddrInfo = userServiceClient
                .getSDDRInfo(userId);
        if (sddrInfo == null)
            throw new GeoException("Directeur SDDR introuvable !");

        Integer idServiceDep = (Integer) sddrInfo
                .get("idServiceDepartementale");
        if (idServiceDep == null)
            throw new GeoException(
                    "Directeur SDDR sans service départemental !");

        // 2. Récupérer le département via le service
        var service = serviceDepRepository
                .findById(idServiceDep)
                .orElseThrow(() -> new GeoException(
                        "Service départemental introuvable !"));

        // 3. Retourner AVEC surfaceCultivee ✅
        return toResponse(service.getDepartement(), true);
    }

    // ── MODIFIER ──────────────────────────────────────────
    @Transactional
    public MessageResponse update(Integer id,
                                  DepartementRequests request) {
        Departement departement = departementRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Département introuvable"));

        if (request.getNomDepartement() != null)
            departement.setNomDepartement(request.getNomDepartement());
        if (request.getPopulation() != null)
            departement.setPopulation(request.getPopulation());
        if (request.getSuperficie() != null)
            departement.setSuperficie(request.getSuperficie());
        if (request.getIdRegion() != null) {
            Region region = regionRepository
                    .findById(request.getIdRegion())
                    .orElseThrow(() ->
                            new GeoException("Région introuvable"));
            departement.setRegion(region);
        }

        departementRepository.save(departement);
        return new MessageResponse(
                "Département modifié avec succès", true);
    }

    // ── SUPPRIMER ─────────────────────────────────────────
    @Transactional
    public MessageResponse delete(Integer id) {
        Departement departement = departementRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Département introuvable"));
        departementRepository.delete(departement);
        return new MessageResponse(
                "Département supprimé avec succès", true);
    }

    // ── MÉTHODE UTILITAIRE ────────────────────────────────
    private DepartementResponse toResponse(
            Departement d, boolean avecSurface) {

        DepartementResponse response = new DepartementResponse();
        response.setIdDepartement(d.getIdDepartement());
        response.setNomDepartement(d.getNomDepartement());
        response.setPopulation(d.getPopulation());
        response.setSuperficie(d.getSuperficie());
        response.setNomRegion(d.getRegion().getNomRegion());
        response.setLatitude(d.getLatitude());
        response.setLongitude(d.getLongitude());
        // ✅ surfaceCultivee SEULEMENT pour Directeur SDDR
        if (avecSurface) {
            Double surface = cultureServiceClient
                    .getSurfaceCultiveeDepartement(
                            d.getIdDepartement().longValue(),
                            LocalDate.now().getYear());
            response.setSurfaceCultivee(surface);
        } else {
            response.setSurfaceCultivee(null);
        }

        if (d.getServiceDepartementale() != null) {
            response.setNomServiceDepartementale(
                    d.getServiceDepartementale().getNomService());
        }
        return response;
    }
}
