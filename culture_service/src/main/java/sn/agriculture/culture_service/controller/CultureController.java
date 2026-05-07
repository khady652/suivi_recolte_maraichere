package sn.agriculture.culture_service.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.culture_service.dtos.request.CultureRequest;
import sn.agriculture.culture_service.dtos.response.CultureResponse;
import sn.agriculture.culture_service.service.AlertService;
import sn.agriculture.culture_service.service.CultureService;
import sn.agriculture.culture_service.client.GeoServiceClient;

import java.util.List;

    @RestController
    @RequestMapping("/api/culture/cultures")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class CultureController {

        private final CultureService cultureService;
        private final GeoServiceClient geoServiceClient;
        private final AlertService alerteRecolteService;
        // ── CRÉER ─────────────────────────────────────────────
        @PostMapping
        public ResponseEntity<CultureResponse> creer(
                @Valid @RequestBody CultureRequest request,
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            String role = authentication.getAuthorities()
                    .iterator().next().getAuthority()
                    .replace("ROLE_", "");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(cultureService.creer(request,
                            userId.longValue(), role));
        }

        // ── LIRE PAR PARCELLE ─────────────────────────────────
        @GetMapping("/parcelle/{idParcel}")
        public ResponseEntity<List<CultureResponse>> getByParcelle(
                @PathVariable Long idParcel) {
            return ResponseEntity.ok(
                    cultureService.getByParcelle(idParcel));
        }

        // ── LIRE MES CULTURES (Agriculteur) ───────────────────
        @GetMapping("/mes-cultures")
        public ResponseEntity<List<CultureResponse>> getMesCultures(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    cultureService.getByAgriculteur(userId.longValue()));
        }

        // ── LIRE PAR DEPARTEMENT (Directeur SDDR) ─────────────
        @GetMapping("/mon-departement")
        public ResponseEntity<List<CultureResponse>> getByDepartement(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    cultureService.getByDirecteurSDDR(userId.longValue()));
        }

        // ── LIRE PAR REGION (Directeur DR) ────────────────────
        @GetMapping("/ma-region")
        public ResponseEntity<List<CultureResponse>> getByRegion(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    cultureService.getByDirecteurDR(userId.longValue()));
        }

        // ── LIRE MA COOPERATIVE (Chef Coopératif) ─────────────
        @GetMapping("/ma-cooperative")
        public ResponseEntity<List<CultureResponse>> getByCooperative(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    cultureService.getByChef(userId.longValue()));
        }

        // ── LIRE UNE CULTURE ──────────────────────────────────
        @GetMapping("/{id}")
        public ResponseEntity<CultureResponse> getById(
                @PathVariable Long id) {
            return ResponseEntity.ok(cultureService.getById(id));
        }

        // ── MODIFIER ──────────────────────────────────────────
        @PutMapping("/{id}")
        public ResponseEntity<CultureResponse> modifier(
                @PathVariable Long id,
                @Valid @RequestBody CultureRequest request) {
            return ResponseEntity.ok(
                    cultureService.modifier(id, request));
        }

        // ── SUPPRIMER ─────────────────────────────────────────
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> supprimer(
                @PathVariable Long id) {
            cultureService.supprimer(id);
            return ResponseEntity.noContent().build();
        }
        @GetMapping("/test-alerte/{idCulture}")
        public ResponseEntity<String> testerAlerte(
                @PathVariable Long idCulture) {
            alerteRecolteService.testerAlerte(idCulture);
            return ResponseEntity.ok(
                    "Alerte SMS envoyée pour culture " + idCulture);
        }
    }

