package sn.agriculture.culture_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.agriculture.culture_service.client.GeoServiceClient;
import sn.agriculture.culture_service.client.UserServiceClient;
import sn.agriculture.culture_service.dtos.response.HistoriqueCultureResponse;
import sn.agriculture.culture_service.dtos.response.RapportAgricoleResponse;
import sn.agriculture.culture_service.entity.Recolte;
import sn.agriculture.culture_service.repository.CultureRepos;
import sn.agriculture.culture_service.repository.ParcelleRepos;
import sn.agriculture.culture_service.repository.RecoltRepos;
import sn.agriculture.culture_service.util.DepartementResponse;
import sn.agriculture.culture_service.util.RegionResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RapportService {

    private final CultureRepos cultureRepository;
    private final RecoltRepos recolteRepository;
    private final ParcelleRepos parcelleRepository;
    private final GeoServiceClient geoServiceClient;
    private final UserServiceClient userServiceClient;

    // ── RAPPORT DIRECTEUR SDDR ────────────────────────────
    public RapportAgricoleResponse getRapportSDDR(
            Integer userId, Integer annee) {

        // 1. Infos directeur
        Map<String, Object> sddrInfo = userServiceClient
                .getSDDRInfo(userId);
        if (sddrInfo == null)
            throw new RuntimeException(
                    "Directeur SDDR introuvable !");

        String nomDirecteur = (String) sddrInfo.get("nom");
        String prenomDirecteur = (String) sddrInfo.get("prenom");
        Integer idServiceDep = (Integer) sddrInfo
                .get("idServiceDepartementale");

        // 2. Infos département
        DepartementResponse dep = geoServiceClient
                .getDepartementByServiceId(idServiceDep);

        // 3. Surface cultivée
        Double surfaceCultivee = cultureRepository
                .surfaceAnneeCourante(
                        List.of(dep.getIdDepartement().longValue()),
                        annee);

        // 4. Récoltes (filtrées par campagne : Oct année-1 → Août année)
        LocalDate[] bornes = bornesCampagne(annee);
        List<Recolte> recoltes = recolteRepository
                .findByCampagneAndDepartements(
                        bornes[0], bornes[1],
                        List.of(dep.getIdDepartement().longValue()));

        // 5. Historique surface
        List<HistoriqueCultureResponse> historiqueSurface =
                cultureRepository
                        .historiqueSurfaceParDepartements(
                                List.of(dep.getIdDepartement()
                                        .longValue()))
                        .stream()
                        .map(row -> HistoriqueCultureResponse.builder()
                                .annee(((Number) row[0]).intValue())
                                .surfaceCultivee(
                                        ((Number) row[1]).doubleValue())
                                .build())
                        .collect(Collectors.toList());

        // 6. Historique production (regroupé par année de campagne)
        Map<String, Double> historiqueProduction = recolteRepository
                .findAll()
                .stream()
                .filter(r -> r.getCulture().getParcelle()
                        .getIdDepartement()
                        .equals(dep.getIdDepartement().longValue()))
                .collect(Collectors.groupingBy(
                        r -> String.valueOf(anneeCampagneDe(r.getDateRecolte())),
                        Collectors.summingDouble(
                                Recolte::getQuantiteRecolte)));

        return buildRapport(
                annee,
                dep.getNomDepartement(),
                "DEPARTEMENT",
                nomDirecteur,
                prenomDirecteur,
                dep.getSuperficie(),
                surfaceCultivee,
                recoltes,
                historiqueSurface,
                historiqueProduction);
    }

    // ── RAPPORT DIRECTEUR DR ──────────────────────────────
    public RapportAgricoleResponse getRapportDR(
            Integer userId, Integer annee) {

        // 1. Infos directeur
        Map<String, Object> drInfo = userServiceClient
                .getDRInfo(userId);
        if (drInfo == null)
            throw new RuntimeException(
                    "Directeur DR introuvable !");

        String nomDirecteur = (String) drInfo.get("nom");
        String prenomDirecteur = (String) drInfo.get("prenom");
        Integer idServiceReg = (Integer) drInfo
                .get("idServiceRegional");

        // 2. Infos région
        Integer idRegion = geoServiceClient
                .getIdRegionByServiceId(idServiceReg);
        List<Long> idDepartements = geoServiceClient
                .getIdDepartementsByRegion(idRegion);
        RegionResponse regionInfo = geoServiceClient
                .getRegionById(idRegion);

        // 3. Surface cultivée
        Double surfaceCultivee = cultureRepository
                .surfaceAnneeCourante(idDepartements, annee);

        // 4. Récoltes (filtrées par campagne)
        LocalDate[] bornes = bornesCampagne(annee);
        List<Recolte> recoltes = recolteRepository
                .findByCampagneAndDepartements(
                        bornes[0], bornes[1], idDepartements);

        // 5. Historique surface
        List<HistoriqueCultureResponse> historiqueSurface =
                cultureRepository
                        .historiqueSurfaceParDepartements(
                                idDepartements)
                        .stream()
                        .map(row -> HistoriqueCultureResponse.builder()
                                .annee(((Number) row[0]).intValue())
                                .surfaceCultivee(
                                        ((Number) row[1]).doubleValue())
                                .build())
                        .collect(Collectors.toList());

        // 6. Historique production (regroupé par année de campagne)
        Map<String, Double> historiqueProduction = recolteRepository
                .findAll()
                .stream()
                .filter(r -> idDepartements.contains(
                        r.getCulture().getParcelle()
                                .getIdDepartement()))
                .collect(Collectors.groupingBy(
                        r -> String.valueOf(anneeCampagneDe(r.getDateRecolte())),
                        Collectors.summingDouble(
                                Recolte::getQuantiteRecolte)));

        return buildRapport(
                annee,
                regionInfo.getNomRegion(),
                "REGION",
                nomDirecteur,
                prenomDirecteur,
                regionInfo.getSuperficie(),
                surfaceCultivee,
                recoltes,
                historiqueSurface,
                historiqueProduction);
    }

    // ── RAPPORT NATIONAL DÉCIDEUR ARM ─────────────────────
    public RapportAgricoleResponse getRapportNational(Integer annee) {

        // 1. Tous les départements depuis geo-service
        List<Long> tousLesDepartements = geoServiceClient
                .getAllDepartements()
                .stream()
                .map(d -> d.getIdDepartement().longValue())
                .toList();

        // 2. Surface cultivée nationale
        Double surfaceCultivee = cultureRepository
                .surfaceAnneeCourante(
                        tousLesDepartements, annee);

        // 3. Toutes les récoltes de la campagne
        LocalDate[] bornes = bornesCampagne(annee);
        List<Recolte> recoltes = recolteRepository
                .findByCampagne(bornes[0], bornes[1]);

        // 4. Historique surface
        List<HistoriqueCultureResponse> historiqueSurface =
                cultureRepository
                        .historiqueSurfaceParDepartements(
                                tousLesDepartements)
                        .stream()
                        .map(row -> HistoriqueCultureResponse.builder()
                                .annee(((Number) row[0]).intValue())
                                .surfaceCultivee(
                                        ((Number) row[1]).doubleValue())
                                .build())
                        .collect(Collectors.toList());

        // 5. Historique production (regroupé par année de campagne)
        Map<String, Double> historiqueProduction = recolteRepository
                .findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        r -> String.valueOf(anneeCampagneDe(r.getDateRecolte())),
                        Collectors.summingDouble(
                                Recolte::getQuantiteRecolte)));

        return buildRapport(
                annee,
                "Sénégal",
                "NATIONAL",
                "Décideur",
                "ARM",
                null,
                surfaceCultivee,
                recoltes,
                historiqueSurface,
                historiqueProduction);
    }

    // ── BUILD RAPPORT ─────────────────────────────────────
    private RapportAgricoleResponse buildRapport(
            Integer annee,
            String territoire,
            String typeTerritoire,
            String nomDirecteur,
            String prenomDirecteur,
            Double superficieTotale,
            Double surfaceCultivee,
            List<Recolte> recoltes,
            List<HistoriqueCultureResponse> historiqueSurface,
            Map<String, Double> historiqueProduction) {

        // Productions
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

        // Production par culture
        Map<String, Double> productionParCulture = recoltes.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCulture().getType(),
                        Collectors.summingDouble(
                                Recolte::getQuantiteRecolte)));

        // Production par variété
        Map<String, Double> productionParVariete = recoltes.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCulture().getVariete(),
                        Collectors.summingDouble(
                                Recolte::getQuantiteRecolte)));

        // Production par saison (casse normalisée pour éviter les doublons)
        Map<String, Double> productionParSaison = recoltes.stream()
                .collect(Collectors.groupingBy(
                        r -> normaliserSaison(r.getCulture().getSaison()),
                        Collectors.summingDouble(
                                Recolte::getQuantiteRecolte)));

        // Alertes
        long culturesEnRetard = cultureRepository.findAll()
                .stream()
                .filter(c -> c.getDatePremierRecoltePrevu() != null
                        && c.getDatePremierRecoltePrevu()
                        .isBefore(LocalDate.now())
                        && c.getRecoltes().isEmpty())
                .count();

        long recoltesSousSeuil = recoltes.stream()
                .filter(r -> r.getCulture()
                        .getQuantiteRecoltePrevu() != null
                        && r.getQuantiteRecolte() < r.getCulture()
                        .getQuantiteRecoltePrevu() * 0.7)
                .count();

        // Taux occupation
        double tauxOccupation = superficieTotale != null
                && superficieTotale > 0
                && surfaceCultivee != null
                ? (surfaceCultivee / superficieTotale) * 100 : 0;

        return RapportAgricoleResponse.builder()
                .annee(annee)
                .territoire(territoire)
                .typeTerritoire(typeTerritoire)
                .nomDirecteur(nomDirecteur)
                .prenomDirecteur(prenomDirecteur)
                .dateGeneration(LocalDate.now())
                .superficieTotale(superficieTotale)
                .surfaceCultivee(surfaceCultivee != null
                        ? surfaceCultivee : 0.0)
                .tauxOccupation(Math.round(tauxOccupation * 10.0)
                        / 10.0)
                .totalProduitKg(totalProduit)
                .totalPrevuKg(totalPrevu)
                .tauxRealisation(Math.round(taux) + "%")
                .nombreRecoltes(recoltes.size())
                .productionParCulture(productionParCulture)
                .productionParVariete(productionParVariete)
                .productionParSaison(productionParSaison)
                .culturesEnRetard(culturesEnRetard)
                .recoltesSousSeuil(recoltesSousSeuil)
                .historiqueSurface(historiqueSurface)
                .historiqueProduction(historiqueProduction)
                .build();
    }

    // ── UTILITAIRES CAMPAGNE ───────────────────────────────

    // Bornes de la campagne (Oct année-1 → Août année)
    private LocalDate[] bornesCampagne(int anneeCampagne) {
        return new LocalDate[] {
                LocalDate.of(anneeCampagne - 1, 10, 1),
                LocalDate.of(anneeCampagne, 8, 31)
        };
    }

    // Convertit une date en "année de campagne" (Oct, Nov, Déc → campagne année+1)
    private int anneeCampagneDe(LocalDate date) {
        return date.getMonthValue() >= 10
                ? date.getYear() + 1
                : date.getYear();
    }

    // Normalise la casse d'une saison (ex: "hivernage" et "Hivernage" → "Hivernage")
    private String normaliserSaison(String saison) {
        if (saison == null || saison.isBlank()) return "Inconnue";
        String s = saison.trim().toLowerCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}