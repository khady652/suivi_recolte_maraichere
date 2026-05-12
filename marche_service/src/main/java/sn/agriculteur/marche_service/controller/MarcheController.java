package sn.agriculteur.marche_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.agriculteur.marche_service.dto.request.MarcheRequest;
import sn.agriculteur.marche_service.dto.response.MarcheResponse;
import sn.agriculteur.marche_service.service.MarcheService;

import java.util.List;

    @RestController
    @RequestMapping("/api/marche/marches")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class MarcheController {

        private final MarcheService marcheService;

        // ── CRÉER ─────────────────────────────────────────────
        @PostMapping
        public ResponseEntity<MarcheResponse> creer(
                @Valid @RequestBody MarcheRequest request) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(marcheService.creer(request));
        }

        // ── LIRE TOUS ─────────────────────────────────────────
        @GetMapping
        public ResponseEntity<List<MarcheResponse>> getAll() {
            return ResponseEntity.ok(marcheService.getAll());
        }

        // ── LIRE UN ───────────────────────────────────────────
        @GetMapping("/{id}")
        public ResponseEntity<MarcheResponse> getById(
                @PathVariable Integer id) {
            return ResponseEntity.ok(marcheService.getById(id));
        }

        // ── LIRE PAR TYPE ─────────────────────────────────────
        @GetMapping("/type/{type}")
        public ResponseEntity<List<MarcheResponse>> getByType(
                @PathVariable String type) {
            return ResponseEntity.ok(marcheService.getByType(type));
        }

        // ── LIRE PAR LIEU ─────────────────────────────────────
        @GetMapping("/lieu/{lieu}")
        public ResponseEntity<List<MarcheResponse>> getByLieu(
                @PathVariable String lieu) {
            return ResponseEntity.ok(marcheService.getByLieu(lieu));
        }

        // ── MODIFIER ──────────────────────────────────────────
        @PutMapping("/{id}")
        public ResponseEntity<MarcheResponse> modifier(
                @PathVariable Integer id,
                @Valid @RequestBody MarcheRequest request) {
            return ResponseEntity.ok(
                    marcheService.modifier(id, request));
        }

        // ── SUPPRIMER ─────────────────────────────────────────
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> supprimer(
                @PathVariable Integer id) {
            marcheService.supprimer(id);
            return ResponseEntity.noContent().build();
        }

    }

