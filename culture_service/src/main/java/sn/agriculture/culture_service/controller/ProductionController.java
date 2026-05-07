package sn.agriculture.culture_service.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.culture_service.service.ProductionService;
import sn.agriculture.culture_service.util.CultureAvancementResponse;
import sn.agriculture.culture_service.util.ProductionReponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

    @RestController
    @RequestMapping("/api/culture/productions")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class ProductionController {

        private final ProductionService productionService;

        // ── TABLEAU DE BORD ───────────────────────────────────
        // GET /api/culture/productions/tableau-de-bord
        // GET /api/culture/productions/tableau-de-bord?annee=2025
        @GetMapping("/tableau-de-bord")
        public ResponseEntity<ProductionReponse> getTableauDeBord(
                @RequestParam(required = false) Integer annee) {
            // ✅ Par défaut → année en cours
            int anneeRecherche = annee != null
                    ? annee
                    : LocalDate.now().getYear();
            return ResponseEntity.ok(
                    productionService.getTableauDeBord(anneeRecherche));
        }

        // ── PRODUCTION PAR RÉGION ─────────────────────────────
        // ── PRODUCTION PAR RÉGION ─────────────────────────────
        @GetMapping("/par-region")
        public ResponseEntity<Map<String, Double>> getParRegion(
                @RequestParam(required = false) Integer annee) {
            int anneeRecherche = annee != null
                    ? annee
                    : LocalDate.now().getYear();
            // ✅ Utiliser getTableauDeBord et extraire productionParRegion
            return ResponseEntity.ok(
                    productionService.getTableauDeBord(anneeRecherche)
                            .getProductionParRegion());
        }

        // ── PRODUCTION PAR ANNÉE ──────────────────────────────
        @GetMapping("/par-annee")
        public ResponseEntity<Map<String, Double>> getParAnnee() {
            return ResponseEntity.ok(
                    productionService.getProductionParAnnee());
        }

        // ── ALERTES ───────────────────────────────────────────
        @GetMapping("/alertes")
        public ResponseEntity<Map<String, Object>> getAlertes(
                @RequestParam(required = false) Integer annee) {
            int anneeRecherche = annee != null
                    ? annee
                    : LocalDate.now().getYear();
            return ResponseEntity.ok(
                    productionService.getAlertes(anneeRecherche));
        }
        @GetMapping("/suivi-cultures")
        public ResponseEntity<List<CultureAvancementResponse>>
        getSuiviCultures() {
            return ResponseEntity.ok(
                    productionService.getSuiviCulturesParRegion());
        }

        // ── CHEF COOPERATIF → sa coopérative ─────────────────
        @GetMapping("/ma-cooperative/tableau-de-bord")
        public ResponseEntity<ProductionReponse> getTableauDeBordCoop(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    productionService.getTableauDeBordCooperative(
                            userId.longValue()));
        }

        @GetMapping("/ma-cooperative/suivi-cultures")
        public ResponseEntity<List<CultureAvancementResponse>>
        getSuiviCulturesCoop(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    productionService.getSuiviCulturesCooperative(
                            userId.longValue()));
        }
    }

