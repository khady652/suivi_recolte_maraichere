package sn.agriculture.culture_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.culture_service.client.UserServiceClient;
import sn.agriculture.culture_service.client.GeoServiceClient;
import sn.agriculture.culture_service.dtos.request.RecolteRequest;
import sn.agriculture.culture_service.dtos.response.RecolteResponse;
import sn.agriculture.culture_service.service.RecolteService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
    @RequestMapping("/api/culture/recoltes")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class RecolteController {

        private final RecolteService recolteService;
        private final UserServiceClient userServiceClient;
        private final GeoServiceClient geoServiceClient;

        // ── ENREGISTRER ───────────────────────────────────────
        @PostMapping
        public ResponseEntity<RecolteResponse> enregistrer(
                @Valid @RequestBody RecolteRequest request,
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            String role = authentication.getAuthorities()
                    .iterator().next().getAuthority()
                    .replace("ROLE_", "");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(recolteService.enregistrer(
                            request, userId.longValue(), role));
        }

        // ── MES RÉCOLTES (Agriculteur) ────────────────────────
        @GetMapping("/mes-recoltes")
        public ResponseEntity<List<RecolteResponse>> getMesRecoltes(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    recolteService.getByAgriculteur(userId.longValue()));
        }

        // ── PAR CULTURE ───────────────────────────────────────
        @GetMapping("/culture/{idCulture}")
        public ResponseEntity<List<RecolteResponse>> getByCulture(
                @PathVariable Long idCulture) {
            return ResponseEntity.ok(
                    recolteService.getByCulture(idCulture));
        }

        // ── MON DÉPARTEMENT (Directeur SDDR) ──────────────────
        @GetMapping("/mon-departement")
        public ResponseEntity<List<RecolteResponse>> getMonDepartement(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            Map<String, Object> sddrInfo = userServiceClient
                    .getSDDRInfo(userId);
            Integer idServiceDep = (Integer) sddrInfo
                    .get("idServiceDepartementale");
            var departement = geoServiceClient
                    .getDepartementByServiceId(idServiceDep);
            return ResponseEntity.ok(
                    recolteService.getByDepartement(
                            departement.getIdDepartement().longValue()));
        }

        // ── MA RÉGION (Directeur DR) ──────────────────────────
        @GetMapping("/ma-region")
        public ResponseEntity<List<RecolteResponse>> getMaRegion(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            Map<String, Object> drInfo = userServiceClient
                    .getDRInfo(userId);
            Integer idServiceRegional = (Integer) drInfo
                    .get("idServiceRegional");
            Integer idRegion = geoServiceClient
                    .getIdRegionByServiceId(idServiceRegional);
            List<Long> idDepartements = geoServiceClient
                    .getIdDepartementsByRegion(idRegion);
            return ResponseEntity.ok(
                    recolteService.getByRegion(idDepartements));
        }

        // ── MA COOPÉRATIVE (Chef Coopératif) ──────────────────
        @GetMapping("/ma-cooperative")
        public ResponseEntity<List<RecolteResponse>> getMaCooperative(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            List<Long> idAgriculteurs = userServiceClient
                    .getMesAgriculteurs(userId)
                    .stream()
                    .map(a -> a.getIdUtilisateur().longValue())
                    .toList();
            return ResponseEntity.ok(
                    recolteService.getByChef(idAgriculteurs));
        }

        // ── TOUTES LES RÉCOLTES (Décideur ARM) ────────────────
        @GetMapping("/toutes")
        public ResponseEntity<List<RecolteResponse>> getToutes() {
            return ResponseEntity.ok(recolteService.getAll());
        }

        // ── STATISTIQUES PAR VARIÉTÉ ──────────────────────────
        @GetMapping("/stats/par-variete")
        public ResponseEntity<Map<String, Double>> getStatsByVariete(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            String role = authentication.getAuthorities()
                    .iterator().next().getAuthority()
                    .replace("ROLE_", "");
            List<RecolteResponse> recoltes = getRecoltesByRole(
                    userId, role);
            return ResponseEntity.ok(
                    recolteService.getStatistiquesParVariete(recoltes));
        }

        // ── STATISTIQUES PAR TYPE ─────────────────────────────
        @GetMapping("/stats/par-type")
        public ResponseEntity<Map<String, Double>> getStatsByType(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            String role = authentication.getAuthorities()
                    .iterator().next().getAuthority()
                    .replace("ROLE_", "");
            List<RecolteResponse> recoltes = getRecoltesByRole(
                    userId, role);
            return ResponseEntity.ok(
                    recolteService.getStatistiquesParType(recoltes));
        }

        // ── COMPARER PRÉVU VS RÉEL ────────────────────────────
        @GetMapping("/stats/prevu-vs-reel")
        public ResponseEntity<Map<String, Object>> getPrevuVsReel(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            String role = authentication.getAuthorities()
                    .iterator().next().getAuthority()
                    .replace("ROLE_", "");
            List<RecolteResponse> recoltes = getRecoltesByRole(
                    userId, role);
            return ResponseEntity.ok(
                    recolteService.comparerPrevuReel(recoltes));
        }

        // ── MÉTHODE UTILITAIRE ────────────────────────────────
        private List<RecolteResponse> getRecoltesByRole(
                Integer userId, String role) {
            return switch (role) {
                case "AGRICULTEUR" ->
                        recolteService.getByAgriculteur(userId.longValue());
                case "CHEF_COOPERATIF" -> {
                    List<Long> ids = userServiceClient
                            .getMesAgriculteurs(userId)
                            .stream()
                            .map(a -> a.getIdUtilisateur().longValue())
                            .toList();
                    yield recolteService.getByChef(ids);
                }
                case "DIRECTEUR_SDDR" -> {
                    Map<String, Object> sddrInfo = userServiceClient
                            .getSDDRInfo(userId);
                    Integer idServiceDep = (Integer) sddrInfo
                            .get("idServiceDepartementale");
                    var dep = geoServiceClient
                            .getDepartementByServiceId(idServiceDep);
                    yield recolteService.getByDepartement(
                            dep.getIdDepartement().longValue());
                }
                case "DIRECTEUR_DR" -> {
                    Map<String, Object> drInfo = userServiceClient
                            .getDRInfo(userId);
                    Integer idServiceReg = (Integer) drInfo
                            .get("idServiceRegional");
                    Integer idRegion = geoServiceClient
                            .getIdRegionByServiceId(idServiceReg);
                    List<Long> deps = geoServiceClient
                            .getIdDepartementsByRegion(idRegion);
                    yield recolteService.getByRegion(deps);
                }
                default -> recolteService.getAll();
            };
        }
        // ── STATISTIQUES PAR RÉGION (Décideur ARM) ────────────
        @GetMapping("/stats/par-region")
        public ResponseEntity<Map<String, Double>> getStatsByRegion() {
            // 1. Récupérer toutes les récoltes
            List<RecolteResponse> toutes = recolteService.getAll();

            // 2. Grouper par région via idDepartement
            Map<String, Double> statsByRegion = toutes.stream()
                    .collect(Collectors.groupingBy(
                            r -> {
                                // Récupérer le nom de la région
                                var dep = geoServiceClient
                                        .getDepartementById(
                                                r.getIdDepartement()
                                                        .intValue());
                                return dep != null ?
                                        dep.getNomRegion() : "Inconnue";
                            },
                            Collectors.summingDouble(
                                    RecolteResponse::getQuantiteRecolte)));

            return ResponseEntity.ok(statsByRegion);
        }

    }

