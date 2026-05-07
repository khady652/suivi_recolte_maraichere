package sn.agriculture.culture_service.util;
import lombok.*;
import java.util.Map;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ProductionReponse {


        // ── Vue globale ───────────────────────────────────────
        private Double totalProduitsKg;
        private Double totalPrevuKg;
        private String tauxRealisation;

        private Integer nombreRecoltes;

        // ── Par région ────────────────────────────────────────
        private Map<String, Double> productionParRegion;

        // ── Par variété ───────────────────────────────────────
        private Map<String, Double> productionParVariete;

        // ── Par saison ────────────────────────────────────────
        private Map<String, Double> productionParSaison;


    }

