package sn.agriculteur.marche_service.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculteur.marche_service.Client.CultureServiceClient;
import sn.agriculteur.marche_service.dto.response.PrevisionClientResponse;
import sn.agriculteur.marche_service.dto.response.StockAlertResponse;
import sn.agriculteur.marche_service.entity.Alerte;
import sn.agriculteur.marche_service.entity.AlerteLecture;
import sn.agriculteur.marche_service.repository.AlerteLectureRepos;
import sn.agriculteur.marche_service.repository.AlerteRepos;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlerteService {

    private final AlerteRepos alerteRepository;
    private final AlerteLectureRepos alerteLectureRepos;
    private final StokAlertService stockAlertService;
    private final CultureServiceClient cultureServiceClient;

    private static final double CONSOMMATION_MENSUELLE = 30000.0;

    // ── GÉNÉRER TOUTES LES ALERTES ────────────────────────
    @Transactional
    public void genererAlertes(String produit) {
        genererAlertePhase1(produit);
        genererAlertePhase2(produit);
        genererAlertePhase3(produit);
    }

    // ── PHASE 1 — Prévision campagne ──────────────────────
    private void genererAlertePhase1(String produit) {
        try {
            PrevisionClientResponse prev = cultureServiceClient
                    .getPrevision(produit);

            if (prev == null || prev.getProductionPrevueTonnes() == 0)
                return;

            String titre = "Prévision campagne " +
                    java.time.LocalDate.now().getYear();

            String message = String.format(
                    "Production estimée : %.1f t sur %s.",
                    prev.getProductionPrevueTonnes(),
                    prev.getPeriodeRecolte());

            String recommandation =
                    "Limiter les autorisations d'importation " +
                            "avant le début des récoltes.";

            sauvegarderAlerteSiNouvelle(Alerte.builder()
                    .produit(produit)
                    .phase(1)
                    .niveau("INFO")
                    .titre(titre)
                    .message(message)
                    .recommandation(recommandation)
                    .valeurPrincipale(prev.getProductionPrevueTonnes())
                    .dateCreation(LocalDateTime.now())
                    .build());

        } catch (Exception e) {
            log.error("Erreur phase 1 : {}", e.getMessage());
        }
    }

    // ── PHASE 2 — Récoltes vs Prévisions ─────────────────
    private void genererAlertePhase2(String produit) {
        try {
            PrevisionClientResponse prev = cultureServiceClient
                    .getPrevision(produit);
            Map<String, Double> stats = cultureServiceClient
                    .getStatsParRegion();

            if (prev == null || stats == null || stats.isEmpty())
                return;

            double totalRecolte = stats.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum() / 1000.0;

            if (totalRecolte == 0) return;

            double taux = (totalRecolte /
                    prev.getProductionPrevueTonnes()) * 100;
            boolean conforme = taux >= 75;
            boolean superieur = taux > 100;

            String niveau = superieur ? "SURPLUS" :
                    conforme  ? "CONFORME" : "DEFICIT";

            String titre = superieur
                    ? "Production supérieure aux prévisions"
                    : conforme
                    ? "Production conforme aux prévisions"
                    : "Risque de déficit de production";

            String message = String.format(
                    "Prévision : %.1f t | Récolte réelle : %.1f t" +
                            " | Taux : %.1f %%",
                    prev.getProductionPrevueTonnes(),
                    totalRecolte, taux);

            String recommandation = superieur
                    ? "Étudier les opportunités d'exportation."
                    : conforme
                    ? "La campagne suit les tendances prévues."
                    : "Prévoir des importations complémentaires.";

            sauvegarderAlerteSiNouvelle(Alerte.builder()
                    .produit(produit)
                    .phase(2)
                    .niveau(niveau)
                    .titre(titre)
                    .message(message)
                    .recommandation(recommandation)
                    .valeurPrincipale(taux)
                    .dateCreation(LocalDateTime.now())
                    .build());

        } catch (Exception e) {
            log.error("Erreur phase 2 : {}", e.getMessage());
        }
    }

    // ── PHASE 3 — Stock national ──────────────────────────
    private void genererAlertePhase3(String produit) {
        try {
            StockAlertResponse stock = stockAlertService
                    .calculerStockAlert(produit);

            double moisCouverts = stock.getMoisCouverts();
            String niveau, titre, recommandation;

            if (moisCouverts > 5) {
                niveau = "EXCEDENT";
                titre = "Surplus détecté — Opportunité d'exportation";
                recommandation = "Étudier les opportunités " +
                        "d'exportation vers les marchés sous-régionaux.";
            } else if (moisCouverts >= 3) {
                niveau = "OK";
                titre = "Situation normale";
                recommandation = "Aucune mesure particulière requise.";
            } else if (moisCouverts >= 1) {
                niveau = "VIGILANCE";
                titre = "Vigilance — Stock limité";
                recommandation = "Surveiller l'évolution " +
                        "des prix et des stocks.";
            } else {
                niveau = "DEFICIT";
                titre = "Risque de pénurie — Action urgente";
                recommandation = "Prévoir des importations urgentes.";
            }

            sauvegarderAlerteSiNouvelle(Alerte.builder()
                    .produit(produit)
                    .phase(3)
                    .niveau(niveau)
                    .titre(titre)
                    .message(stock.getMessage())
                    .recommandation(recommandation)
                    .valeurPrincipale(stock.getStockTotalTonnes())
                    .dateCreation(LocalDateTime.now())
                    .build());

        } catch (Exception e) {
            log.error("Erreur phase 3 : {}", e.getMessage());
        }
    }

    // ── ÉVITER DOUBLONS ───────────────────────────────────
    private void sauvegarderAlerteSiNouvelle(Alerte alerte) {
        boolean existeDeja = alerteRepository
                .existsByPhaseAndNiveauAndDateCreationAfter(
                        alerte.getPhase(),
                        alerte.getNiveau(),
                        LocalDateTime.now().minusHours(24));
        if (!existeDeja) {
            alerteRepository.save(alerte);
            log.info("Alerte sauvegardée : Phase {} - {}",
                    alerte.getPhase(), alerte.getNiveau());
        }
    }

    // ── ALERTES NON LUES PAR DÉCIDEUR ─────────────────────
    public List<Alerte> getAlertesNonLues(Integer idDecideur) {
        return alerteRepository.findAllByOrderByDateCreationDesc()
                .stream()
                .filter(a -> !alerteLectureRepos
                        .existsByAlerteIdAndIdDecideur(
                                a.getId(), idDecideur))
                .collect(Collectors.toList());
    }

    // ── COMPTER NON LUES ──────────────────────────────────
    public long countNonLues(Integer idDecideur) {
        return alerteRepository.findAllByOrderByDateCreationDesc()
                .stream()
                .filter(a -> !alerteLectureRepos
                        .existsByAlerteIdAndIdDecideur(
                                a.getId(), idDecideur))
                .count();
    }

    // ── MARQUER UNE ALERTE LUE ────────────────────────────
    @Transactional
    public void marquerCommeLue(Integer idAlerte,
                                Integer idDecideur) {
        if (!alerteLectureRepos.existsByAlerteIdAndIdDecideur(
                idAlerte, idDecideur)) {
            alerteRepository.findById(idAlerte).ifPresent(a -> {
                alerteLectureRepos.save(AlerteLecture.builder()
                        .alerte(a)
                        .idDecideur(idDecideur)
                        .dateLecture(LocalDateTime.now())
                        .build());
            });
        }
    }

    // ── MARQUER TOUTES LUES ───────────────────────────────
    @Transactional
    public void marquerToutesCommeLues(Integer idDecideur) {
        alerteRepository.findAllByOrderByDateCreationDesc()
                .forEach(a -> marquerCommeLue(
                        a.getId(), idDecideur));
    }

    // ── HISTORIQUE ────────────────────────────────────────
    public List<Alerte> getHistorique() {
        return alerteRepository.findAllByOrderByDateCreationDesc();
    }
}