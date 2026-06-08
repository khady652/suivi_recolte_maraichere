package sn.agriculture.culture_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.culture_service.client.GeoServiceClient;
import sn.agriculture.culture_service.client.UserServiceClient;
import sn.agriculture.culture_service.dtos.request.CultureRequest;
import sn.agriculture.culture_service.dtos.response.CultureResponse;
import sn.agriculture.culture_service.entity.Culture;
import sn.agriculture.culture_service.entity.Parcelle;
import sn.agriculture.culture_service.exception.CultureException;
import sn.agriculture.culture_service.repository.CultureRepos;
import sn.agriculture.culture_service.repository.ParcelleRepos;
import sn.agriculture.culture_service.util.AgriculteurResponse;
import sn.agriculture.culture_service.util.DepartementResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CultureService {

    private final CultureRepos         cultureRepository;
    private final ParcelleRepos        parcelleRepository;
    private final UserServiceClient    userServiceClient;
    private final GeoServiceClient     geoServiceClient;
    private final MlPredictService  mlPredictionService; // ← AJOUT

    // ── CRÉER ─────────────────────────────────────────────

    @Transactional
    public CultureResponse creer(CultureRequest request,
                                 Long userId, String role) {

        Parcelle parcelle = parcelleRepository
                .findById(request.getIdParcel())
                .orElseThrow(() -> new CultureException(
                        "Parcelle introuvable !"));

        if (role.equals("AGRICULTEUR") &&
                !parcelle.getIdAgriculteur().equals(userId)) {
            throw new CultureException(
                    "Cette parcelle ne vous appartient pas !");
        }

        // ✅ Calcul automatique date récolte
        LocalDate dateRecoltePrevu = calculerDateRecolte(
                request.getType(), request.getDateSemence());

        // ✅ Prédiction ML quantiteRecoltePrevu
        Double quantitePredite = null;
        try {
            quantitePredite = mlPredictionService
                    .predireQuantiteRecolte(request, parcelle);
            log.info("Prédiction ML : {} kg", quantitePredite);
        } catch (Exception e) {
            log.warn("Prédiction ML échouée : {} — " +
                    "quantiteRecoltePrevu sera null", e.getMessage());
        }

        Culture culture = Culture.builder()
                .type(request.getType())
                .variete(request.getVariete())
                .dateSemence(request.getDateSemence())
                .datePremierRecoltePrevu(dateRecoltePrevu)
                .typeIrrigation(request.getTypeIrrigation())
                .quantiteSeme(request.getQuantiteSeme())
                .superficiCultive(request.getSuperficiCultive())
                .saison(request.getSaison())
                .intraUtilise(request.getIntraUtilise())
                .fumureOrganique(request.getFumureOrganique())

                .quantiteRecoltePrevu(quantitePredite) // ← ML
                .parcelle(parcelle)
                .build();

        cultureRepository.save(culture);
        log.info("Culture {} créée sur parcelle {} - " +
                        "Récolte prévue : {} - Quantité : {} kg",
                request.getType(), request.getIdParcel(),
                dateRecoltePrevu, quantitePredite);

        return toResponse(culture);
    }

    // ── CALCUL DATE RÉCOLTE ───────────────────────────────

    private LocalDate calculerDateRecolte(
            String type, LocalDate dateSemence) {
        if (dateSemence == null) return null;
        return switch (type.toLowerCase()) {
            case "oignon"   -> dateSemence.plusMonths(4);
            case "tomate"   -> dateSemence.plusMonths(3);
            case "mil"      -> dateSemence.plusMonths(3);
            case "arachide" -> dateSemence.plusMonths(4);
            case "mais"     -> dateSemence.plusMonths(3);
            case "niebe"    -> dateSemence.plusMonths(2);
            default         -> dateSemence.plusMonths(3);
        };
    }

    // ── LIRE PAR PARCELLE ─────────────────────────────────

    public List<CultureResponse> getByParcelle(Long idParcel) {
        return cultureRepository
                .findByParcelle_IdParcel(idParcel)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE PAR AGRICULTEUR ──────────────────────────────

    public List<CultureResponse> getByAgriculteur(Long idAgriculteur) {
        return parcelleRepository
                .findByIdAgriculteur(idAgriculteur)
                .stream()
                .flatMap(p -> cultureRepository
                        .findByParcelle_IdParcel(p.getIdParcel())
                        .stream())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE PAR DEPARTEMENT ──────────────────────────────

    public List<CultureResponse> getByDepartement(Long idDepartement) {
        return parcelleRepository
                .findByIdDepartement(idDepartement)
                .stream()
                .flatMap(p -> cultureRepository
                        .findByParcelle_IdParcel(p.getIdParcel())
                        .stream())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE PAR REGION ───────────────────────────────────

    public List<CultureResponse> getByRegion(List<Long> idDepartements) {
        return parcelleRepository
                .findByIdDepartementIn(idDepartements)
                .stream()
                .flatMap(p -> cultureRepository
                        .findByParcelle_IdParcel(p.getIdParcel())
                        .stream())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE UN ───────────────────────────────────────────

    public CultureResponse getById(Long id) {
        Culture culture = cultureRepository
                .findById(id)
                .orElseThrow(() -> new CultureException(
                        "Culture introuvable !"));
        return toResponse(culture);
    }

    // ── MODIFIER ──────────────────────────────────────────

    @Transactional
    public CultureResponse modifier(Long id, CultureRequest request) {
        Culture culture = cultureRepository
                .findById(id)
                .orElseThrow(() -> new CultureException(
                        "Culture introuvable !"));

        if (request.getType() != null)
            culture.setType(request.getType());
        if (request.getVariete() != null)
            culture.setVariete(request.getVariete());
        if (request.getDateSemence() != null)
            culture.setDateSemence(request.getDateSemence());
        if (request.getTypeIrrigation() != null)
            culture.setTypeIrrigation(request.getTypeIrrigation());
        if (request.getQuantiteSeme() != null)
            culture.setQuantiteSeme(request.getQuantiteSeme());
        if (request.getSuperficiCultive() != null)
            culture.setSuperficiCultive(request.getSuperficiCultive());
        if (request.getSaison() != null)
            culture.setSaison(request.getSaison());


        // ✅ Recalculer la prédiction ML si modification
        try {
            Parcelle parcelle = culture.getParcelle();
            Double quantitePredite = mlPredictionService
                    .predireQuantiteRecolte(request, parcelle);
            if (quantitePredite != null) {
                culture.setQuantiteRecoltePrevu(quantitePredite);
                log.info("Prédiction ML mise à jour : {} kg",
                        quantitePredite);
            }
        } catch (Exception e) {
            log.warn("Prédiction ML échouée lors modification : {}",
                    e.getMessage());
        }

        cultureRepository.save(culture);
        return toResponse(culture);
    }

    // ── SUPPRIMER ─────────────────────────────────────────

    @Transactional
    public void supprimer(Long id) {
        cultureRepository.findById(id)
                .orElseThrow(() -> new CultureException(
                        "Culture introuvable !"));
        cultureRepository.deleteById(id);
    }

    // ── MAPPER ────────────────────────────────────────────

    private CultureResponse toResponse(Culture c) {
        return CultureResponse.builder()
                .idCulture(c.getIdCulture())
                .type(c.getType())
                .variete(c.getVariete())
                .dateSemence(c.getDateSemence())
                .datePremierRecoltePrevu(c.getDatePremierRecoltePrevu())
                .typeIrrigation(c.getTypeIrrigation())
                .quantiteSeme(c.getQuantiteSeme())
                .superficiCultive(c.getSuperficiCultive())
                .saison(c.getSaison())
                .quantiteRecoltePrevu(c.getQuantiteRecoltePrevu())
                .intraUtilise(c.getIntraUtilise())
                .fumureOrganique(c.getFumureOrganique())
                .idParcel(c.getParcelle().getIdParcel())
                .nomParcelle(c.getParcelle().getNomParcelle())
                .lieu(c.getParcelle().getLieu())
                .build();
    }

    // ── LIRE PAR DIRECTEUR SDDR ───────────────────────────

    public List<CultureResponse> getByDirecteurSDDR(Long userId) {
        Map<String, Object> sddrInfo = userServiceClient
                .getSDDRInfo(userId.intValue());
        if (sddrInfo == null)
            throw new CultureException("Directeur SDDR introuvable !");
        Integer idServiceDep = (Integer) sddrInfo
                .get("idServiceDepartementale");
        if (idServiceDep == null)
            throw new CultureException(
                    "Directeur SDDR sans service départemental !");
        DepartementResponse departement = geoServiceClient
                .getDepartementByServiceId(idServiceDep);
        if (departement == null)
            throw new CultureException("Département introuvable !");
        return getByDepartement(
                departement.getIdDepartement().longValue());
    }

    // ── LIRE PAR DIRECTEUR DR ─────────────────────────────

    public List<CultureResponse> getByDirecteurDR(Long userId) {
        Map<String, Object> drInfo = userServiceClient
                .getDRInfo(userId.intValue());
        if (drInfo == null)
            throw new CultureException("Directeur DR introuvable !");
        Integer idServiceRegional = (Integer) drInfo
                .get("idServiceRegional");
        if (idServiceRegional == null)
            throw new CultureException(
                    "Directeur DR sans service régional !");
        Integer idRegion = geoServiceClient
                .getIdRegionByServiceId(idServiceRegional);
        List<Long> idDepartements = geoServiceClient
                .getIdDepartementsByRegion(idRegion);
        return getByRegion(idDepartements);
    }

    // ── LIRE PAR CHEF COOPERATIF ──────────────────────────

    public List<CultureResponse> getByChef(Long idChef) {
        List<AgriculteurResponse> agriculteurs = userServiceClient
                .getMesAgriculteurs(idChef.intValue());
        if (agriculteurs.isEmpty())
            return List.of();
        return agriculteurs.stream()
                .flatMap(a -> getByAgriculteur(
                        a.getIdUtilisateur().longValue()).stream())
                .collect(Collectors.toList());
    }
}