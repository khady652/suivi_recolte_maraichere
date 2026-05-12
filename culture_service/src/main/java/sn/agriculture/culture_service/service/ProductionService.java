package sn.agriculture.culture_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.culture_service.client.GeoServiceClient;
import sn.agriculture.culture_service.client.UserServiceClient;
import sn.agriculture.culture_service.dtos.response.HistoriqueCultureResponse;
import sn.agriculture.culture_service.entity.Culture;
import sn.agriculture.culture_service.entity.Recolte;
import sn.agriculture.culture_service.repository.CultureRepos;
import sn.agriculture.culture_service.repository.ParcelleRepos;
import sn.agriculture.culture_service.repository.RecoltRepos;
import sn.agriculture.culture_service.util.CultureAvancementResponse;
import sn.agriculture.culture_service.util.ProductionReponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionService {

    private final RecoltRepos recolteRepository;
    private final CultureRepos cultureRepository;
    private final ParcelleRepos parcelleRepository;
    private final GeoServiceClient geoServiceClient;
    private final UserServiceClient userServiceClient;

    // ── TABLEAU DE BORD DÉCIDEUR ──────────────────────────
    @Transactional
    public ProductionReponse getTableauDeBord(int annee) {
        List<Recolte> recoltes = recolteRepository
                .findByAnnee(annee);
        return buildProductionResponse(recoltes);
    }

    // ── TABLEAU DE BORD DIRECTEUR DR ─────────────────────
    @Transactional
    public ProductionReponse getTableauDeBordRegion(Long userId) {
        Map<String, Object> drInfo = userServiceClient
                .getDRInfo(userId.intValue());
        Integer idServiceRegional = (Integer) drInfo
                .get("idServiceRegional");
        Integer idRegion = geoServiceClient
                .getIdRegionByServiceId(idServiceRegional);
        List<Long> idDepartements = geoServiceClient
                .getIdDepartementsByRegion(idRegion);
        int annee = LocalDate.now().getYear();
        List<Recolte> recoltes = recolteRepository
                .findByAnneeAndDepartements(annee, idDepartements);
        return buildProductionResponse(recoltes);
    }

    // ── TABLEAU DE BORD DIRECTEUR SDDR ───────────────────
    @Transactional
    public ProductionReponse getTableauDeBordDepartement(
            Long userId) {
        Map<String, Object> sddrInfo = userServiceClient
                .getSDDRInfo(userId.intValue());
        Integer idServiceDep = (Integer) sddrInfo
                .get("idServiceDepartementale");
        var dep = geoServiceClient
                .getDepartementByServiceId(idServiceDep);
        int annee = LocalDate.now().getYear();
        List<Recolte> recoltes = recolteRepository
                .findByAnneeAndDepartements(annee,
                        List.of(dep.getIdDepartement().longValue()));
        return buildProductionResponse(recoltes);
    }

    // ── PRODUCTION PAR ANNÉE ──────────────────────────────
    @Transactional
    public Map<String, Double> getProductionParAnnee() {
        return recolteRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        r -> String.valueOf(
                                r.getDateRecolte().getYear()),
                        Collectors.summingDouble(
                                Recolte::getQuantiteRecolte)));
    }

    // ── ALERTES PRODUCTION ────────────────────────────────
    @Transactional
    public Map<String, Object> getAlertes(int annee) {
        List<Recolte> recoltes = recolteRepository
                .findByAnnee(annee);

        long culturesEnRetard = cultureRepository.findAll()
                .stream()
                .filter(c -> c.getDatePremierRecoltePrevu() != null
                        && c.getDatePremierRecoltePrevu()
                        .isBefore(LocalDate.now())
                        && c.getRecoltes().isEmpty())
                .count();

        long ecartImportant = recoltes.stream()
                .filter(r -> r.getCulture()
                        .getQuantiteRecoltePrevu() != null
                        && r.getQuantiteRecolte() < r.getCulture()
                        .getQuantiteRecoltePrevu() * 0.7)
                .count();

        return Map.of(
                "culturesEnRetard", culturesEnRetard,
                "recoltesSousSeuil", ecartImportant,
                "message", culturesEnRetard > 0
                        ? culturesEnRetard +
                        " cultures en retard de récolte !"
                        : "Aucune alerte ✅"
        );
    }

    // ── SUIVI CULTURES DÉCIDEUR ───────────────────────────
    @Transactional
    public List<CultureAvancementResponse>
    getSuiviCulturesParRegion() {
        int anneeEnCours = LocalDate.now().getYear();
        LocalDate aujourd_hui = LocalDate.now();

        List<Culture> cultures = cultureRepository.findAll()
                .stream()
                .filter(c -> c.getDateSemence() != null && (
                        c.getDateSemence().getYear() == anneeEnCours
                                || (c.getDatePremierRecoltePrevu() != null &&
                                c.getDatePremierRecoltePrevu().getYear()
                                        == anneeEnCours)))
                .collect(Collectors.toList());

        Map<String, List<Culture>> byRegion = cultures.stream()
                .collect(Collectors.groupingBy(c -> {
                    try {
                        var dep = geoServiceClient
                                .getDepartementById(
                                        c.getParcelle()
                                                .getIdDepartement()
                                                .intValue());
                        return dep != null ?
                                dep.getNomRegion() : "Inconnue";
                    } catch (Exception e) {
                        return "Inconnue";
                    }
                }));

        return buildAvancement(byRegion, aujourd_hui);
    }

    // ── SUIVI CULTURES DIRECTEUR DR ───────────────────────
    @Transactional
    public List<CultureAvancementResponse> getSuiviCulturesRegion(
            Long userId) {
        Map<String, Object> drInfo = userServiceClient
                .getDRInfo(userId.intValue());
        Integer idServiceRegional = (Integer) drInfo
                .get("idServiceRegional");
        Integer idRegion = geoServiceClient
                .getIdRegionByServiceId(idServiceRegional);
        List<Long> idDepartements = geoServiceClient
                .getIdDepartementsByRegion(idRegion);
        return getSuiviCulturesParDepartements(idDepartements);
    }

    // ── SUIVI CULTURES DIRECTEUR SDDR ────────────────────
    @Transactional
    public List<CultureAvancementResponse>
    getSuiviCulturesDepartement(Long userId) {
        Map<String, Object> sddrInfo = userServiceClient
                .getSDDRInfo(userId.intValue());
        Integer idServiceDep = (Integer) sddrInfo
                .get("idServiceDepartementale");
        var dep = geoServiceClient
                .getDepartementByServiceId(idServiceDep);
        return getSuiviCulturesParDepartements(
                List.of(dep.getIdDepartement().longValue()));
    }

    // ── SUIVI CULTURES PAR DÉPARTEMENTS ──────────────────
    @Transactional
    public List<CultureAvancementResponse>
    getSuiviCulturesParDepartements(
            List<Long> idDepartements) {
        int anneeEnCours = LocalDate.now().getYear();
        LocalDate aujourd_hui = LocalDate.now();

        List<Culture> cultures = parcelleRepository
                .findByIdDepartementIn(idDepartements)
                .stream()
                .flatMap(p -> cultureRepository
                        .findByParcelle_IdParcel(p.getIdParcel())
                        .stream())
                .filter(c -> c.getDateSemence() != null && (
                        c.getDateSemence().getYear() == anneeEnCours
                                || (c.getDatePremierRecoltePrevu() != null &&
                                c.getDatePremierRecoltePrevu().getYear()
                                        == anneeEnCours)))
                .collect(Collectors.toList());

        Map<String, List<Culture>> byDep = cultures.stream()
                .collect(Collectors.groupingBy(c -> {
                    try {
                        var dep = geoServiceClient
                                .getDepartementById(
                                        c.getParcelle()
                                                .getIdDepartement()
                                                .intValue());
                        return dep != null ?
                                dep.getNomDepartement() : "Inconnu";
                    } catch (Exception e) {
                        return "Inconnu";
                    }
                }));

        return buildAvancement(byDep, aujourd_hui);
    }

    // ── RÉCOLTES PAR ANNÉE ────────────────────────────────
    public List<Recolte> getRecoltesByAnnee(int annee) {
        return recolteRepository.findByAnnee(annee);
    }

    // ── MÉTHODE UTILITAIRE — BUILD PRODUCTION ─────────────
    private ProductionReponse buildProductionResponse(
            List<Recolte> recoltes) {

        double totalProduit = recoltes.stream()
                .mapToDouble(Recolte::getQuantiteRecolte)
                .sum();

        double totalPrevu = recoltes.stream()
                .mapToDouble(r -> r.getCulture()
                        .getQuantiteRecoltePrevu() != null
                        ? r.getCulture().getQuantiteRecoltePrevu()
                        : 0)
                .sum();

        double taux = totalPrevu > 0
                ? (totalProduit / totalPrevu) * 100 : 0;

        Map<String, Double> parVariete = recoltes.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCulture().getVariete(),
                        Collectors.summingDouble(
                                Recolte::getQuantiteRecolte)));

        Map<String, Double> parSaison = recoltes.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCulture().getSaison() != null
                                ? r.getCulture().getSaison()
                                : "Inconnue",
                        Collectors.summingDouble(
                                Recolte::getQuantiteRecolte)));

        Map<String, Double> parRegion = recoltes.stream()
                .collect(Collectors.groupingBy(
                        r -> {
                            try {
                                var dep = geoServiceClient
                                        .getDepartementById(
                                                r.getCulture()
                                                        .getParcelle()
                                                        .getIdDepartement()
                                                        .intValue());
                                return dep != null ?
                                        dep.getNomRegion() : "Inconnue";
                            } catch (Exception e) {
                                return "Inconnue";
                            }
                        },
                        Collectors.summingDouble(
                                Recolte::getQuantiteRecolte)));

        /*long nombreParcelles = recoltes.stream()
                .map(r -> r.getCulture().getParcelle().getIdParcel())
                .distinct().count();
*/
        return ProductionReponse.builder()
                .totalProduitsKg(totalProduit)
                .totalPrevuKg(totalPrevu)
                .tauxRealisation(Math.round(taux) + "%")
                .nombreRecoltes(recoltes.size())

                .productionParRegion(parRegion)
                .productionParVariete(parVariete)
                .productionParSaison(parSaison)
                .build();
    }

    // ── MÉTHODE UTILITAIRE — BUILD AVANCEMENT ─────────────
    private List<CultureAvancementResponse> buildAvancement(
            Map<String, List<Culture>> grouped,
            LocalDate aujourd_hui) {
        return grouped.entrySet().stream()
                .map(entry -> {
                    String nom = entry.getKey();
                    List<Culture> culturesList = entry.getValue();
                    int total = culturesList.size();

                    int enCours = 0, prete = 0,
                            enRetard = 0, recoltee = 0,
                            planifiee = 0;
                    double totalAvancement = 0;

                    for (Culture c : culturesList) {
                        String statut = calculerStatut(
                                c, aujourd_hui);
                        switch (statut) {
                            case "EN_COURS"  -> enCours++;
                            case "PRETE"     -> prete++;
                            case "EN_RETARD" -> enRetard++;
                            case "RECOLTEE"  -> recoltee++;
                            case "PLANIFIEE" -> planifiee++;
                        }
                        totalAvancement += calculerAvancement(
                                c, aujourd_hui);
                    }

                    double avancementMoyen = total > 0
                            ? totalAvancement / total : 0;

                    return CultureAvancementResponse.builder()
                            .nomRegion(nom)
                            .totalCultures(total)
                            .enCours(enCours)
                            .prete(prete)
                            .enRetard(enRetard)
                            .recoltee(recoltee)
                            .planifiee(planifiee)
                            .pourcentageEnCours(total > 0
                                    ? (enCours * 100.0) / total : 0)
                            .pourcentagePrete(total > 0
                                    ? (prete * 100.0) / total : 0)
                            .pourcentageEnRetard(total > 0
                                    ? (enRetard * 100.0) / total : 0)
                            .pourcentageRecoltee(total > 0
                                    ? (recoltee * 100.0) / total : 0)
                            .pourcentagePlanifiee(total > 0
                                    ? (planifiee * 100.0) / total : 0)
                            .avancementMoyenPourcent(avancementMoyen)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ── CALCULER STATUT ───────────────────────────────────
    private String calculerStatut(Culture c,
                                  LocalDate aujourd_hui) {
        if (!c.getRecoltes().isEmpty()) return "RECOLTEE";
        if (c.getDateSemence().isAfter(aujourd_hui))
            return "PLANIFIEE";
        if (c.getDatePremierRecoltePrevu() == null)
            return "EN_COURS";
        if (aujourd_hui.isAfter(c.getDatePremierRecoltePrevu()))
            return "EN_RETARD";
        long joursRestants = aujourd_hui.until(
                c.getDatePremierRecoltePrevu(),
                java.time.temporal.ChronoUnit.DAYS);
        if (joursRestants <= 30) return "PRETE";
        return "EN_COURS";
    }

    // ── CALCULER AVANCEMENT % ─────────────────────────────
    private double calculerAvancement(Culture c,
                                      LocalDate aujourd_hui) {
        if (c.getDatePremierRecoltePrevu() == null) return 0;
        if (!c.getRecoltes().isEmpty()) return 100;

        long totalJours = c.getDateSemence().until(
                c.getDatePremierRecoltePrevu(),
                java.time.temporal.ChronoUnit.DAYS);
        long joursEcoules = c.getDateSemence().until(
                aujourd_hui,
                java.time.temporal.ChronoUnit.DAYS);

        if (totalJours <= 0) return 0;
        return Math.min(100,
                (joursEcoules * 100.0) / totalJours);
    }
    // ── TABLEAU DE BORD CHEF COOPERATIF ──────────────────
    @Transactional
    public ProductionReponse getTableauDeBordCooperative(
            Long userId) {
        // 1. Récupérer les agriculteurs du chef
        List<Long> idAgriculteurs = userServiceClient
                .getMesAgriculteurs(userId.intValue())
                .stream()
                .map(a -> a.getIdUtilisateur().longValue())
                .toList();

        // 2. Récupérer les parcelles de ces agriculteurs
        List<Long> idDepartements = parcelleRepository
                .findByIdAgriculteurIn(idAgriculteurs)
                .stream()
                .map(p -> p.getIdDepartement())
                .distinct()
                .toList();

        // 3. Récoltes de ces agriculteurs
        int annee = LocalDate.now().getYear();
        List<Recolte> recoltes = recolteRepository
                .findByAnneeAndDepartements(annee, idDepartements)
                .stream()
                .filter(r -> idAgriculteurs.contains(
                        r.getCulture().getParcelle()
                                .getIdAgriculteur()))
                .toList();

        return buildProductionResponse(recoltes);
    }

    // ── SUIVI CULTURES CHEF COOPERATIF ────────────────────
    @Transactional
    public List<CultureAvancementResponse>
    getSuiviCulturesCooperative(Long userId) {
        // 1. Récupérer les agriculteurs du chef
        List<Long> idAgriculteurs = userServiceClient
                .getMesAgriculteurs(userId.intValue())
                .stream()
                .map(a -> a.getIdUtilisateur().longValue())
                .toList();

        // 2. Récupérer les cultures de ces agriculteurs
        int anneeEnCours = LocalDate.now().getYear();
        LocalDate aujourd_hui = LocalDate.now();

        List<Culture> cultures = parcelleRepository
                .findByIdAgriculteurIn(idAgriculteurs)
                .stream()
                .flatMap(p -> cultureRepository
                        .findByParcelle_IdParcel(p.getIdParcel())
                        .stream())
                .filter(c -> c.getDateSemence() != null && (
                        c.getDateSemence().getYear() == anneeEnCours
                                || (c.getDatePremierRecoltePrevu() != null &&
                                c.getDatePremierRecoltePrevu().getYear()
                                        == anneeEnCours)))
                .collect(Collectors.toList());

        // 3. Grouper par agriculteur
        Map<String, List<Culture>> byAgriculteur = cultures.stream()
                .collect(Collectors.groupingBy(c -> {
                    try {
                        var info = userServiceClient
                                .getAgriculteurInfo(
                                        c.getParcelle()
                                                .getIdAgriculteur()
                                                .intValue());
                        return info != null ?
                                info.get("nom") + " " +
                                        info.get("prenom") : "Inconnu";
                    } catch (Exception e) {
                        return "Inconnu";
                    }
                }));

        return buildAvancement(byAgriculteur, aujourd_hui);
    }
    public HistoriqueCultureResponse getSurfaceAnneeCourante(
            List<Long> idDepartements,
            String nomTerritoire,
            String typeTerritoire) {

        int annee = LocalDate.now().getYear();
        Double surface = cultureRepository
                .surfaceAnneeCourante(idDepartements, annee);

        return HistoriqueCultureResponse.builder()
                .annee(annee)
                .surfaceCultivee(surface != null ? surface : 0.0)

                .build();
    }

    public List<HistoriqueCultureResponse> getHistoriqueSurface(
            List<Long> idDepartements,
            String nomTerritoire,
            String typeTerritoire) {

        return cultureRepository
                .historiqueSurfaceParDepartements(idDepartements)
                .stream()
                .map(row -> HistoriqueCultureResponse.builder()
                        .annee(((Number) row[0]).intValue())
                        .surfaceCultivee(
                                ((Number) row[1]).doubleValue())

                        .build())
                .collect(Collectors.toList());
    }
}