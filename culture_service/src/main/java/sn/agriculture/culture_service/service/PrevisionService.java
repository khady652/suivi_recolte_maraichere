package sn.agriculture.culture_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.culture_service.client.GeoServiceClient;
import sn.agriculture.culture_service.client.UserServiceClient;
import sn.agriculture.culture_service.dtos.response.PrevisionResponse;
import sn.agriculture.culture_service.entity.Culture;
import sn.agriculture.culture_service.repository.CultureRepos;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PrevisionService {

    private final CultureRepos cultureRepository;
    private final GeoServiceClient geoServiceClient;
    private final UserServiceClient userServiceClient;
    public PrevisionResponse calculerPrevision(String produit) {

        // ── Cultures en cours non récoltées ───────────────
        List<Culture> culturesEnCours = cultureRepository
                .findAll()
                .stream()
                .filter(c -> c.getType()
                        .equalsIgnoreCase(produit))
                .filter(c -> c.getRecoltes().isEmpty())
                .filter(c -> c.getQuantiteRecoltePrevu() != null
                        && c.getDatePremierRecoltePrevu() != null
                        && c.getDatePremierRecoltePrevu()
                        .isAfter(LocalDate.now()))
                .toList();

        // ── Production prévue totale en tonnes ────────────
        double productionPrevue = culturesEnCours.stream()
                .mapToDouble(c ->
                        c.getQuantiteRecoltePrevu() / 1000.0)
                .sum();

        // ── Période de récolte ────────────────────────────
        LocalDate dateMin = culturesEnCours.stream()
                .map(Culture::getDatePremierRecoltePrevu)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate dateMax = culturesEnCours.stream()
                .map(Culture::getDatePremierRecoltePrevu)
                .max(LocalDate::compareTo)
                .orElse(null);

        String periodeRecolte = dateMin != null
                && dateMax != null
                ? dateMin.getMonth().getDisplayName(
                TextStyle.FULL, Locale.FRENCH)
                + " - "
                + dateMax.getMonth().getDisplayName(
                TextStyle.FULL, Locale.FRENCH)
                + " " + dateMax.getYear()
                : "Non définie";

        // ── Message informatif ────────────────────────────
        String message = culturesEnCours.isEmpty()
                ? "Aucune culture d'oignon en cours."
                : String.format(
                "Production prevue d'oignon : " +
                        "%.0f tonnes sur la periode %s.",
                productionPrevue, periodeRecolte);

        // ── Production par mois ───────────────────────────
        Map<String, Double> productionParMois = culturesEnCours
                .stream()
                .collect(Collectors.groupingBy(
                        c -> c.getDatePremierRecoltePrevu()
                                .getMonth().getDisplayName(
                                        TextStyle.FULL,
                                        Locale.FRENCH)
                                + " "
                                + c.getDatePremierRecoltePrevu()
                                .getYear(),
                        Collectors.summingDouble(c ->
                                c.getQuantiteRecoltePrevu()
                                        / 1000.0)));

        // ── Production par région ─────────────────────────
        Map<String, Double> productionParRegion = culturesEnCours
                .stream()
                .collect(Collectors.groupingBy(
                        c -> {
                            try {
                                var dep = geoServiceClient
                                        .getDepartementById(
                                                c.getParcelle()
                                                        .getIdDepartement()
                                                        .intValue());
                                return dep != null
                                        ? dep.getNomRegion()
                                        : "Inconnue";
                            } catch (Exception e) {
                                return "Inconnue";
                            }
                        },
                        Collectors.summingDouble(c ->
                                c.getQuantiteRecoltePrevu()
                                        / 1000.0)));

        log.info("Prévision {} : {} t sur {}",
                produit, productionPrevue, periodeRecolte);

        return PrevisionResponse.builder()
                .produit(produit)
                .productionPrevueTonnes(
                        Math.round(productionPrevue * 10.0)
                                / 10.0)
                .periodeRecolte(periodeRecolte)
                .message(message)
                .productionParMois(productionParMois)
                .productionParRegion(productionParRegion)
                .build();
    }
    public PrevisionResponse calculerPrevisionCooperative(Long userId) {
        // 1. Récupérer les agriculteurs du chef
        List<Long> idAgriculteurs = userServiceClient
                .getMesAgriculteurs(userId.intValue())
                .stream()
                .map(a -> a.getIdUtilisateur().longValue())
                .toList();

        // 2. Filtrer les cultures de ces agriculteurs
        List<Culture> culturesEnCours = cultureRepository
                .findAll()
                .stream()
                .filter(c -> c.getType().equalsIgnoreCase("oignon"))
                .filter(c -> c.getRecoltes().isEmpty())
                .filter(c -> c.getQuantiteRecoltePrevu() != null
                        && c.getDatePremierRecoltePrevu() != null
                        && c.getDatePremierRecoltePrevu().isAfter(LocalDate.now()))
                .filter(c -> idAgriculteurs.contains(
                        c.getParcelle().getIdAgriculteur()))
                .toList();

        // 3. Calcul identique à calculerPrevision()
        double productionPrevue = culturesEnCours.stream()
                .mapToDouble(c -> c.getQuantiteRecoltePrevu() / 1000.0)
                .sum();

        LocalDate dateMin = culturesEnCours.stream()
                .map(Culture::getDatePremierRecoltePrevu)
                .min(LocalDate::compareTo).orElse(null);

        LocalDate dateMax = culturesEnCours.stream()
                .map(Culture::getDatePremierRecoltePrevu)
                .max(LocalDate::compareTo).orElse(null);

        String periodeRecolte = dateMin != null && dateMax != null
                ? dateMin.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH)
                + " - "
                + dateMax.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH)
                + " " + dateMax.getYear()
                : "Non définie";

        Map<String, Double> productionParMois = culturesEnCours.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getDatePremierRecoltePrevu()
                                .getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH)
                                + " " + c.getDatePremierRecoltePrevu().getYear(),
                        Collectors.summingDouble(c -> c.getQuantiteRecoltePrevu() / 1000.0)));

        return PrevisionResponse.builder()
                .produit("oignon")
                .productionPrevueTonnes(Math.round(productionPrevue * 10.0) / 10.0)
                .periodeRecolte(periodeRecolte)
                .message(String.format("Production prévue : %.1f t sur %s",
                        productionPrevue, periodeRecolte))
                .productionParMois(productionParMois)
                .productionParRegion(new java.util.HashMap<>())
                .build();
    }
}