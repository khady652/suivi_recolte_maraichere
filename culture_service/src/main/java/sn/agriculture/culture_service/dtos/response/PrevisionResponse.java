package sn.agriculture.culture_service.dtos.response;

import lombok.*;
import java.util.Map;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class PrevisionResponse {

        private String produit;
        private Double productionPrevueTonnes;
        private String periodeRecolte;
        private String message;
        private Map<String, Double> productionParMois;
        private Map<String, Double> productionParRegion;
    }

