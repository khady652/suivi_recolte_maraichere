package sn.agriculteur.marche_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sn.agriculteur.marche_service.Client.UserServiceClient;
import sn.agriculteur.marche_service.dto.response.StockAlertResponse;
import sn.agriculteur.marche_service.repository.CollecteRepos;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class StokAlertService {

        private final CollecteRepos collecteRepository;
        private final UserServiceClient userServiceClient;
        //private final AlerteService alerteService;
        // ── CONSOMMATION MENSUELLE (tonnes/mois) ──────────────
        private static final Map<String, Double>
                CONSOMMATION_MENSUELLE = Map.of(
                "oignon", 30000.0
        );

        // ── SEUILS ────────────────────────────────────────────
        private static final double SEUIL_EXCEDENT = 5.0;
        private static final double SEUIL_DEFICIT  = 1.0;

        // ── CALCULER STOCK ALERT ──────────────────────────────
        public StockAlertResponse calculerStockAlert(
                String produit) {

            String produitLower = produit.toLowerCase();

            if (!CONSOMMATION_MENSUELLE.containsKey(produitLower))
                throw new RuntimeException(
                        "Produit non géré : " + produit);

            double consommation = CONSOMMATION_MENSUELLE
                    .get(produitLower);

            // ── Stock par marché ──────────────────────────────
            Map<String, Double> stockParMarche = collecteRepository
                    .findByProduit(produit)
                    .stream()
                    .collect(Collectors.groupingBy(
                            c -> c.getMarche().getNomMarche(),
                            Collectors.summingDouble(
                                    c -> c.getQuantiteDisponible()
                            )));

            // ── Stock total ───────────────────────────────────
            double stockTotal = stockParMarche.values()
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

            // ── Mois couverts ─────────────────────────────────
            double moisCouverts = consommation > 0
                    ? stockTotal / consommation : 0;

            // ── Niveau et message ─────────────────────────────
            String niveau;
            String message;

            if (moisCouverts > SEUIL_EXCEDENT) {
                niveau = "EXCEDENT";
                message = String.format(
                        "Stock actuel d'oignon dans les marches : " +
                                "%.0f tonnes. " +
                                "Couvre %.1f mois de consommation nationale. " +
                                "EXCEDENT detecte (seuil : %.0f mois). " +
                                "Il est recommande de planifier " +
                                "une exportation.",
                        stockTotal, moisCouverts, SEUIL_EXCEDENT);

            } else if (moisCouverts < SEUIL_DEFICIT) {
                niveau = "DEFICIT";
                message = String.format(
                        "Stock actuel d'oignon dans les marches : " +
                                "%.0f tonnes. " +
                                "Couvre seulement %.1f mois de consommation. " +
                                "DEFICIT detecte (seuil : %.0f mois). " +
                                "Il est recommande de planifier " +
                                "une importation.",
                        stockTotal, moisCouverts, SEUIL_DEFICIT);

            } else {
                niveau = "OK";
                message = String.format(
                        "Stock actuel d'oignon dans les marches : " +
                                "%.0f tonnes. " +
                                "Couvre %.1f mois de consommation nationale. " +
                                "Aucune action necessaire.",
                        stockTotal, moisCouverts);
            }

            log.info("Stock alert {} : {} t → {} mois → {}",
                    produit, stockTotal, moisCouverts, niveau);

            return StockAlertResponse.builder()
                    .produit(produit)
                    .stockTotalTonnes(
                            Math.round(stockTotal * 10.0) / 10.0)
                    //.consommationMensuelle(consommation)
                    .moisCouverts(
                            Math.round(moisCouverts * 10.0) / 10.0)
                    .niveau(niveau)
                    .message(message)
                    .stockParMarche(stockParMarche)
                    .build();
        }

        // ── VÉRIFICATION MENSUELLE AUTOMATIQUE ───────────────
        @Scheduled(cron = "0 0 8 1 * *")
        public void verifierStockMensuel() {
            log.info("Vérification stock mensuel...");

            StockAlertResponse stock = calculerStockAlert("oignon");

            if (!stock.getNiveau().equals("OK")) {
                envoyerAlerteDecideurs(stock.getMessage());
                log.info("Alerte SMS envoyée : {}", stock.getNiveau());
            }

            log.info("Vérification terminée !");
        }
        // ── ENVOYER SMS AUX DÉCIDEURS ─────────────────────────
        private void envoyerAlerteDecideurs(String message) {
            try {
                List<Map<String, String>> decideurs =
                        userServiceClient.getAllDecideurs();

                if (decideurs == null || decideurs.isEmpty()) {
                    log.warn("Aucun décideur trouvé !");
                    return;
                }

                decideurs.forEach(d -> {
                    String telephone = d.get("telephone");
                    if (telephone != null
                            && !telephone.isEmpty()) {
                        userServiceClient.envoyerAlertePrix(
                                Map.of(
                                        "message", message,
                                        "telephone", telephone
                                ));
                        log.info("Alerte envoyée : {}",
                                telephone);
                    }
                });
            } catch (Exception e) {
                log.error("Erreur envoi alerte : {}",
                        e.getMessage());
            }
        }
    }

