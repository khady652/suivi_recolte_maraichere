package sn.agriculture.culture_service.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.culture_service.dtos.response.RapportAgricoleResponse;
import sn.agriculture.culture_service.service.PdfRapportService;
import sn.agriculture.culture_service.service.RapportService;

import java.time.LocalDate;

    @RestController
    @RequestMapping("/api/culture/rapports")
    @RequiredArgsConstructor
    @Slf4j

    public class RapportController {

        private final RapportService rapportService;
        private final PdfRapportService pdfRapportService;

        // ── RAPPORT JSON DIRECTEUR SDDR ───────────────────────
        @GetMapping("/mon-departement")
        public ResponseEntity<RapportAgricoleResponse>
        getRapportSDDR(
                Authentication authentication,
                @RequestParam(required = false) Integer annee) {
            Integer userId = (Integer) authentication.getPrincipal();
            int a = annee != null
                    ? annee : LocalDate.now().getYear();
            return ResponseEntity.ok(
                    rapportService.getRapportSDDR(userId, a));
        }

        // ── RAPPORT PDF DIRECTEUR SDDR ────────────────────────
        @GetMapping("/mon-departement/pdf")
        public ResponseEntity<byte[]> getRapportSDDRPdf(
                Authentication authentication,
                @RequestParam(required = false) Integer annee) {
            Integer userId = (Integer) authentication.getPrincipal();
            int a = annee != null
                    ? annee : LocalDate.now().getYear();

            RapportAgricoleResponse rapport =
                    rapportService.getRapportSDDR(userId, a);
            byte[] pdf = pdfRapportService.genererPdf(rapport);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=rapport_" +
                                    rapport.getTerritoire() + "_" +
                                    a + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }

        // ── RAPPORT JSON DIRECTEUR DR ─────────────────────────
        @GetMapping("/ma-region")
        public ResponseEntity<RapportAgricoleResponse>
        getRapportDR(
                Authentication authentication,
                @RequestParam(required = false) Integer annee) {
            Integer userId = (Integer) authentication.getPrincipal();
            int a = annee != null
                    ? annee : LocalDate.now().getYear();
            return ResponseEntity.ok(
                    rapportService.getRapportDR(userId, a));
        }

        // ── RAPPORT PDF DIRECTEUR DR ──────────────────────────
        @GetMapping("/ma-region/pdf")
        public ResponseEntity<byte[]> getRapportDRPdf(
                Authentication authentication,
                @RequestParam(required = false) Integer annee) {
            Integer userId = (Integer) authentication.getPrincipal();
            int a = annee != null
                    ? annee : LocalDate.now().getYear();

            RapportAgricoleResponse rapport =
                    rapportService.getRapportDR(userId, a);
            byte[] pdf = pdfRapportService.genererPdf(rapport);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=rapport_" +
                                    rapport.getTerritoire() + "_" +
                                    a + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }
        // ── RAPPORT JSON NATIONAL ─────────────────────────────
        @GetMapping("/national")
        public ResponseEntity<RapportAgricoleResponse> getRapportNational(
                @RequestParam(required = false) Integer annee) {
            int a = annee != null
                    ? annee : LocalDate.now().getYear();
            return ResponseEntity.ok(
                    rapportService.getRapportNational(a));
        }

        // ── RAPPORT PDF NATIONAL ──────────────────────────────
        @GetMapping("/national/pdf")
        public ResponseEntity<byte[]> getRapportNationalPdf(
                @RequestParam(required = false) Integer annee) {
            int a = annee != null
                    ? annee : LocalDate.now().getYear();

            RapportAgricoleResponse rapport =
                    rapportService.getRapportNational(a);
            byte[] pdf = pdfRapportService.genererPdf(rapport);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=rapport_national_" +
                                    a + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }
    }

