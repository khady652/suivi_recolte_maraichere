package sn.agriculture.culture_service.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.culture_service.client.GeoServiceClient;
import sn.agriculture.culture_service.client.UserServiceClient;
import sn.agriculture.culture_service.dtos.response.HistoriqueCultureResponse;
import sn.agriculture.culture_service.repository.CultureRepos;
import sn.agriculture.culture_service.service.CultureService;
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
        private final UserServiceClient userServiceClient;
        private final GeoServiceClient  geoServiceClient;
        private final CultureRepos cultureRepos;
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
        // ── SURFACE DIRECTEUR SDDR ────────────────────────────
        @GetMapping("/mon-departement/surface-cultivee")
        public ResponseEntity<HistoriqueCultureResponse>
        getSurfaceDepartement(Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            Map<String, Object> sddrInfo = userServiceClient
                    .getSDDRInfo(userId);
            Integer idServiceDep = (Integer) sddrInfo
                    .get("idServiceDepartementale");
            var dep = geoServiceClient
                    .getDepartementByServiceId(idServiceDep);
            return ResponseEntity.ok(
                    productionService.getSurfaceAnneeCourante(
                            List.of(dep.getIdDepartement().longValue()),
                            dep.getNomDepartement(),
                            "DEPARTEMENT"));
        }

        // ── HISTORIQUE DIRECTEUR SDDR ─────────────────────────
        @GetMapping("/mon-departement/historique")
        public ResponseEntity<List<HistoriqueCultureResponse>>
        getHistoriqueDepartement(Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            Map<String, Object> sddrInfo = userServiceClient
                    .getSDDRInfo(userId);
            Integer idServiceDep = (Integer) sddrInfo
                    .get("idServiceDepartementale");
            var dep = geoServiceClient
                    .getDepartementByServiceId(idServiceDep);
            return ResponseEntity.ok(
                    productionService.getHistoriqueSurface(
                            List.of(dep.getIdDepartement().longValue()),
                            dep.getNomDepartement(),
                            "DEPARTEMENT"));
        }

        // ── SURFACE DIRECTEUR DR ──────────────────────────────
        @GetMapping("/ma-region/surface-cultivee")
        public ResponseEntity<HistoriqueCultureResponse>
        getSurfaceRegion(Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            Map<String, Object> drInfo = userServiceClient
                    .getDRInfo(userId);
            Integer idServiceReg = (Integer) drInfo
                    .get("idServiceRegional");
            Integer idRegion = geoServiceClient
                    .getIdRegionByServiceId(idServiceReg);
            List<Long> idDeps = geoServiceClient
                    .getIdDepartementsByRegion(idRegion);
            return ResponseEntity.ok(
                    productionService.getSurfaceAnneeCourante(
                            idDeps, "Région", "REGION"));
        }

        // ── HISTORIQUE DIRECTEUR DR ───────────────────────────
        @GetMapping("/ma-region/historique")
        public ResponseEntity<List<HistoriqueCultureResponse>>
        getHistoriqueRegion(Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            Map<String, Object> drInfo = userServiceClient
                    .getDRInfo(userId);
            Integer idServiceReg = (Integer) drInfo
                    .get("idServiceRegional");
            Integer idRegion = geoServiceClient
                    .getIdRegionByServiceId(idServiceReg);
            List<Long> idDeps = geoServiceClient
                    .getIdDepartementsByRegion(idRegion);
            return ResponseEntity.ok(
                    productionService.getHistoriqueSurface(
                            idDeps, "Région", "REGION"));
        }
        // ── SURFACE PAR DÉPARTEMENT (appelé par geo-service) ──
        @GetMapping("/departement/{id}/surface-cultivee")
        public ResponseEntity<Double> getSurfaceDepartement(
                @PathVariable Long id,
                @RequestParam(required = false) Integer annee) {
            int a = annee != null ? annee : LocalDate.now().getYear();
            Double surface = cultureRepos
                    .surfaceAnneeCourante(List.of(id), a);
            return ResponseEntity.ok(
                    surface != null ? surface : 0.0);
        }

        // ── SURFACE PAR RÉGION (appelé par geo-service) ───────
        @GetMapping("/region/{id}/surface-cultivee")
        public ResponseEntity<Double> getSurfaceRegion(
                @PathVariable Integer id,
                @RequestParam(required = false) Integer annee) {
            int a = annee != null ? annee : LocalDate.now().getYear();
            List<Long> idDeps = geoServiceClient
                    .getIdDepartementsByRegion(id);
            Double surface = cultureRepos
                    .surfaceAnneeCourante(idDeps, a);
            return ResponseEntity.ok(
                    surface != null ? surface : 0.0);
        }
    }

