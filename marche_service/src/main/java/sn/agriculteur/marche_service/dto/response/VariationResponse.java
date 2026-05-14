package sn.agriculteur.marche_service.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class VariationResponse {

        private String produit;
        private Integer annee;
        private Integer mois;

        // Variation prix
        private Double prixMin;
        private Double prixMax;
        private Double prixMoyen;
        private List<PrixParDate> variationPrix;

        // Variation stock
        private Double stockMin;
        private Double stockMax;
        private Double stockMoyen;
        private List<StockParDate> variationStock;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class PrixParDate {
            private LocalDate date;
            private Double prix;
            private String nomMarche;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class StockParDate {
            private LocalDate date;
            private Double stock;
            private String nomMarche;
        }
    }

