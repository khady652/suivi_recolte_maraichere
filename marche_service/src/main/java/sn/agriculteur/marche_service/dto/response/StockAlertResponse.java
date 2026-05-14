package sn.agriculteur.marche_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class StockAlertResponse {

        private String produit;
        private Double stockTotalTonnes;
        private Double moisCouverts;
        private String niveau;   // "EXCEDENT", "DEFICIT", "OK"
        private String message;
        private Map<String, Double> stockParMarche;
    }


