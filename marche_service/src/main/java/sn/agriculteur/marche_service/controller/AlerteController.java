package sn.agriculteur.marche_service.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.agriculteur.marche_service.entity.Alerte;
import sn.agriculteur.marche_service.service.AlerteService;

import java.util.List;

@RestController
@RequestMapping("/api/marche/alertes")
@RequiredArgsConstructor
public class AlerteController {

    private final AlerteService alerteService;

    // ── ALERTES NON LUES ──────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Alerte>> getNonLues(
            Authentication authentication) {
        Integer idDecideur = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(
                alerteService.getAlertesNonLues(idDecideur));
    }

    // ── COMPTER NON LUES ──────────────────────────────────
    @GetMapping("/count")
    public ResponseEntity<Long> countNonLues(
            Authentication authentication) {
        Integer idDecideur = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(
                alerteService.countNonLues(idDecideur));
    }

    // ── HISTORIQUE ────────────────────────────────────────
    @GetMapping("/historique")
    public ResponseEntity<List<Alerte>> getHistorique() {
        return ResponseEntity.ok(alerteService.getHistorique());
    }

    // ── MARQUER UNE ALERTE LUE ────────────────────────────
    @PutMapping("/{id}/lue")
    public ResponseEntity<Void> marquerLue(
            @PathVariable Integer id,
            Authentication authentication) {
        Integer idDecideur = (Integer) authentication.getPrincipal();
        alerteService.marquerCommeLue(id, idDecideur);
        return ResponseEntity.ok().build();
    }

    // ── MARQUER TOUTES LUES ───────────────────────────────
    @PutMapping("/lire-toutes")
    public ResponseEntity<Void> marquerToutesLues(
            Authentication authentication) {
        Integer idDecideur = (Integer) authentication.getPrincipal();
        alerteService.marquerToutesCommeLues(idDecideur);
        return ResponseEntity.ok().build();
    }

    // ── GÉNÉRER MANUELLEMENT ──────────────────────────────
    @PostMapping("/generer")
    public ResponseEntity<Void> genererManuellement() {
        alerteService.genererAlertes("oignon");
        return ResponseEntity.ok().build();
    }
}
