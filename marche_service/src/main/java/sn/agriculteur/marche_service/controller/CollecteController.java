package sn.agriculteur.marche_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.agriculteur.marche_service.dto.request.CollecteRequest;
import sn.agriculteur.marche_service.dto.response.CollecteResponse;
import sn.agriculteur.marche_service.service.CollecteService;

import java.util.List;
import java.util.Map;

    @RestController
    @RequestMapping("/api/marche/collectes")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class CollecteController {

        private final CollecteService collecteService;

        // ── ENREGISTRER ───────────────────────────────────────
        @PostMapping
        public ResponseEntity<CollecteResponse> enregistrer(
                @Valid @RequestBody CollecteRequest request,
                Authentication authentication) {
            Integer idEnqueteur = (Integer) authentication
                    .getPrincipal();
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(collecteService.enregistrer(
                            request, idEnqueteur));
        }

        // ── MES COLLECTES ─────────────────────────────────────
        @GetMapping("/mes-collectes")
        public ResponseEntity<List<CollecteResponse>> getMesCollectes(
                Authentication authentication) {
            Integer idEnqueteur = (Integer) authentication
                    .getPrincipal();
            return ResponseEntity.ok(
                    collecteService.getMesCollectes(idEnqueteur));
        }

        // ── PAR MARCHÉ ────────────────────────────────────────
        @GetMapping("/marche/{idMarche}")
        public ResponseEntity<List<CollecteResponse>> getByMarche(
                @PathVariable Integer idMarche) {
            return ResponseEntity.ok(
                    collecteService.getByMarche(idMarche));
        }

        // ── PAR PRODUIT ───────────────────────────────────────
        @GetMapping("/produit/{produit}")
        public ResponseEntity<List<CollecteResponse>> getByProduit(
                @PathVariable String produit) {
            return ResponseEntity.ok(
                    collecteService.getByProduit(produit));
        }

        // ── TOUTES ────────────────────────────────────────────
        @GetMapping
        public ResponseEntity<List<CollecteResponse>> getAll() {
            return ResponseEntity.ok(collecteService.getAll());
        }

        // ── MODIFIER ──────────────────────────────────────────
        @PutMapping("/{id}")
        public ResponseEntity<CollecteResponse> modifier(
                @PathVariable Integer id,
                @Valid @RequestBody CollecteRequest request) {
            return ResponseEntity.ok(
                    collecteService.modifier(id, request));
        }

        // ── SUPPRIMER ─────────────────────────────────────────
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> supprimer(
                @PathVariable Integer id) {
            collecteService.supprimer(id);
            return ResponseEntity.noContent().build();
        }

        // ── PRIX MOYEN PAR PRODUIT ────────────────────────────
        @GetMapping("/stats/prix-moyen")
        public ResponseEntity<Map<String, Double>> getPrixMoyen() {
            return ResponseEntity.ok(
                    collecteService.getPrixMoyenParProduit());
        }

        // ── STOCK DU JOUR ─────────────────────────────────────
        @GetMapping("/stats/stock-du-jour")
        public ResponseEntity<Map<String, Double>> getStockDuJour() {
            return ResponseEntity.ok(
                    collecteService.getStockDuJour());
        }

        // ── DERNIERS PRIX ─────────────────────────────────────
        @GetMapping("/derniers-prix")
        public ResponseEntity<List<CollecteResponse>> getDerniersPrix() {
            return ResponseEntity.ok(
                    collecteService.getDerniersPrix());
        }
    }

