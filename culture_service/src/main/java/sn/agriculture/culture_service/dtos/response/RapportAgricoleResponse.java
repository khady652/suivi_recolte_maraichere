package sn.agriculture.culture_service.dtos.response;

import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class RapportAgricoleResponse {

        // ── EN-TÊTE ───────────────────────────────────────────
        private Integer annee;
        private String territoire;
        private String typeTerritoire;
        private String nomDirecteur;
        private String prenomDirecteur;
        private LocalDate dateGeneration;

        // ── SUPERFICIE ────────────────────────────────────────
        private Double superficieTotale;
        private Double surfaceCultivee;
        private Double tauxOccupation;

        // ── PRODUCTIONS ───────────────────────────────────────
        private Double totalProduitKg;
        private Double totalPrevuKg;
        private String tauxRealisation;
        private Integer nombreRecoltes;
        private Map<String, Double> productionParCulture;
        private Map<String, Double> productionParVariete;
        private Map<String, Double> productionParSaison;

        // ── ALERTES ───────────────────────────────────────────
        private Long culturesEnRetard;
        private Long recoltesSousSeuil;

        // ── ÉVOLUTION ANNUELLE ────────────────────────────────
        private List<HistoriqueCultureResponse> historiqueSurface;
        private Map<String, Double> historiqueProduction;
    }

