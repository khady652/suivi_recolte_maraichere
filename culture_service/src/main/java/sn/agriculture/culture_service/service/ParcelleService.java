package sn.agriculture.culture_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.culture_service.client.GeoServiceClient;
import sn.agriculture.culture_service.client.UserServiceClient;
import sn.agriculture.culture_service.dtos.request.ParcelleRequest;
import sn.agriculture.culture_service.dtos.response.ParcelleResponse;
import sn.agriculture.culture_service.entity.Parcelle;
import sn.agriculture.culture_service.exception.CultureException;
import sn.agriculture.culture_service.repository.ParcelleRepos;
import sn.agriculture.culture_service.util.AgriculteurResponse;
import sn.agriculture.culture_service.util.DepartementResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParcelleService {

    private final ParcelleRepos parcelleRepository;
    private final GeoServiceClient geoServiceClient;
    private final UserServiceClient userServiceClient; // ✅ final ajouté

    // ── CRÉER ─────────────────────────────────────────────
    @Transactional
    public ParcelleResponse creerParcelle(ParcelleRequest dto) {
        Parcelle parcelle = Parcelle.builder()
                .nomParcelle(dto.getNomParcelle())
                .lieu(dto.getLieu())
                .superficie(dto.getSuperficie())
                .typeSol(dto.getTypeSol())
                .qualiteSol(dto.getQualiteSol())
                .sourceEau(dto.getSourceEau())
                .estIrriguee(dto.getEstIrriguee())
                .idAgriculteur(dto.getIdAgriculteur())
                .idDepartement(dto.getIdDepartement())
                .build();

        return toResponse(parcelleRepository.save(parcelle));
    }

    // ── MODIFIER ──────────────────────────────────────────
    @Transactional
    public ParcelleResponse modifierParcelle(Long id, ParcelleRequest dto) {
        Parcelle parcelle = parcelleRepository.findById(id)
                .orElseThrow(() -> new CultureException(
                        "Parcelle non trouvée avec l'id : " + id));

        parcelle.setNomParcelle(dto.getNomParcelle());
        parcelle.setLieu(dto.getLieu());
        parcelle.setSuperficie(dto.getSuperficie());
        parcelle.setTypeSol(dto.getTypeSol());
        parcelle.setQualiteSol(dto.getQualiteSol()); // ✅ Ajout
        parcelle.setSourceEau(dto.getSourceEau());
        parcelle.setEstIrriguee(dto.getEstIrriguee());
        parcelle.setIdAgriculteur(dto.getIdAgriculteur());
        parcelle.setIdDepartement(dto.getIdDepartement());

        return toResponse(parcelleRepository.save(parcelle));
    }

    // ── SUPPRIMER ─────────────────────────────────────────
    @Transactional
    public void supprimerParcelle(Long id) {
        parcelleRepository.findById(id)
                .orElseThrow(() -> new CultureException(
                        "Parcelle non trouvée avec l'id : " + id));
        parcelleRepository.deleteById(id);
    }

    // ── AGRICULTEUR → ses propres parcelles ───────────────
    public List<ParcelleResponse> getParcellesByAgriculteur(Long idAgriculteur) {
        return parcelleRepository.findByIdAgriculteur(idAgriculteur)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── DIRECTEUR SDDR → parcelles de son département ─────
    public List<ParcelleResponse> getParcellesByDepartement(Long idDepartement) {
        return parcelleRepository.findByIdDepartement(idDepartement)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── DIRECTEUR DR → parcelles de sa région ─────────────
    public List<ParcelleResponse> getParcellesByRegion(Integer idRegion) {
        List<Long> idDepartements = geoServiceClient
                .getIdDepartementsByRegion(idRegion);
        return parcelleRepository.findByIdDepartementIn(idDepartements)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── CHEF COOPÉRATIF → parcelles de ses membres ────────
    public List<ParcelleResponse> getParcellesByCooperative(
            List<Long> idAgriculteurs) {
        return parcelleRepository.findByIdAgriculteurIn(idAgriculteurs)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── CHEF COOPÉRATIF → via token ───────────────────────
    public List<ParcelleResponse> getParcellesByChef(Long idChef) {

        // 1. Récupérer les agriculteurs du chef
        List<AgriculteurResponse> agriculteurs = userServiceClient
                .getMesAgriculteurs(idChef.intValue()); // ✅ Plus de token

        if (agriculteurs.isEmpty())
            return List.of();

        // 2. Extraire les IDs
        List<Long> idAgriculteurs = agriculteurs.stream()
                .map(a -> a.getIdUtilisateur().longValue())
                .toList();

        // 3. Récupérer les parcelles
        return parcelleRepository
                .findByIdAgriculteurIn(idAgriculteurs)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── ADMIN / DECIDEUR ARM → toutes les parcelles ───────
    public List<ParcelleResponse> getToutesParcelles() {
        return parcelleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE UNE PARCELLE ─────────────────────────────────
    public ParcelleResponse getParcelleById(Long id) {
        Parcelle parcelle = parcelleRepository.findById(id)
                .orElseThrow(() -> new CultureException(
                        "Parcelle non trouvée avec l'id : " + id));
        return toResponse(parcelle);
    }

    // ── MAPPER entité → Response ──────────────────────────
    private ParcelleResponse toResponse(Parcelle parcelle) {

        // ✅ Récupérer nom agriculteur depuis users-service
        String nomAgriculteur = null;
        String prenomAgriculteur = null;
        try {
            Map<String, String> agriculteurInfo = userServiceClient
                    .getAgriculteurInfo(
                            parcelle.getIdAgriculteur().intValue());
            if (agriculteurInfo != null) {
                nomAgriculteur = agriculteurInfo.get("nom");
                prenomAgriculteur = agriculteurInfo.get("prenom");
            }
        } catch (Exception e) {
            log.warn("Agriculteur introuvable : {}",
                    parcelle.getIdAgriculteur());
        }

        // ✅ Récupérer nom département depuis geo-service
        String nomDepartement = null;
        try {
            DepartementResponse departement = geoServiceClient
                    .getDepartementById(
                            parcelle.getIdDepartement().intValue());
            if (departement != null) {
                nomDepartement = departement.getNomDepartement();
            }
        } catch (Exception e) {
            log.warn("Département introuvable : {}",
                    parcelle.getIdDepartement());
        }

        return ParcelleResponse.builder()
                .idParcel(parcelle.getIdParcel())
                .nomParcelle(parcelle.getNomParcelle())
                .lieu(parcelle.getLieu())
                .superficie(parcelle.getSuperficie())
                .typeSol(parcelle.getTypeSol())
                .qualiteSol(parcelle.getQualiteSol())
                .sourceEau(parcelle.getSourceEau())
                .estIrriguee(parcelle.getEstIrriguee())
                .idAgriculteur(parcelle.getIdAgriculteur())
                .idDepartement(parcelle.getIdDepartement())
                .nomAgriculteur(nomAgriculteur)
                .prenomAgriculteur(prenomAgriculteur)
                .nomDepartement(nomDepartement)
                .build();
    }
    public List<ParcelleResponse> getParcellesByDirecteurSDDR(Long userId) {

        // 1. Récupérer idServiceDepartementale du directeur
        Map<String, Object> sddrInfo = userServiceClient
                .getSDDRInfo(userId.intValue());

        if (sddrInfo == null)
            throw new CultureException("Directeur SDDR introuvable !");

        Integer idServiceDep = (Integer) sddrInfo
                .get("idServiceDepartementale");

        if (idServiceDep == null)
            throw new CultureException(
                    "Directeur SDDR sans service départemental !");

        // 2. Récupérer idDepartement depuis geo-service
        DepartementResponse departement = geoServiceClient
                .getDepartementByServiceId(idServiceDep);

        if (departement == null)
            throw new CultureException("Service départemental introuvable !");

        // 3. Récupérer les parcelles du département
        return parcelleRepository
                .findByIdDepartement(
                        departement.getIdDepartement().longValue())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ParcelleResponse> getParcellesByDirecteurDR(Long userId) {

        // 1. Récupérer idServiceRegional du directeur DR
        Map<String, Object> drInfo = userServiceClient
                .getDRInfo(userId.intValue());

        if (drInfo == null)
            throw new CultureException("Directeur DR introuvable !");

        Integer idServiceRegional = (Integer) drInfo
                .get("idServiceRegional");

        if (idServiceRegional == null)
            throw new CultureException(
                    "Directeur DR sans service régional !");

        // 2. Récupérer idRegion depuis geo-service
        Integer idRegion = geoServiceClient
                .getIdRegionByServiceId(idServiceRegional);

        if (idRegion == null)
            throw new CultureException("Service régional introuvable !");

        // 3. Récupérer tous les départements de la région
        List<Long> idDepartements = geoServiceClient
                .getIdDepartementsByRegion(idRegion);

        // 4. Récupérer toutes les parcelles de ces départements
        return parcelleRepository
                .findByIdDepartementIn(idDepartements)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}