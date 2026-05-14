package sn.agriculteur.public_service.Controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.agriculteur.public_service.Response.CollecteResponse;
import sn.agriculteur.public_service.Response.ZoneProduction;
import sn.agriculteur.public_service.service.PublicService;


import java.util.List;
import java.util.Map;

    @RestController
    @RequestMapping("/api/public")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class PublicController {

        private final PublicService publicService;

        // ── CARTE SIG — ZONES DE PRODUCTION ──────────────────
        @GetMapping("/carte/zones-production")
        public ResponseEntity<ZoneProduction>
        getZonesProduction() {
            return ResponseEntity.ok(
                    publicService.getZonesProduction());
        }

        // ── PRIX MARCHÉS ──────────────────────────────────────
        @GetMapping("/marches/prix")
        public ResponseEntity<List<CollecteResponse>> getPrix() {
            return ResponseEntity.ok(
                    publicService.getPrixMarches());
        }

        // ── STOCK DU JOUR ─────────────────────────────────────
        @GetMapping("/marches/stock")
        public ResponseEntity<Map<String, Double>> getStock() {
            return ResponseEntity.ok(
                    publicService.getStockDuJour());
        }
    }

