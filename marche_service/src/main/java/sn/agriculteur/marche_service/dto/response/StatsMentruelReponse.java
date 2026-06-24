package sn.agriculteur.marche_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class StatsMentruelReponse {
        private Integer moisNum;
        private String moisNom;
        private Double prixMoyen;
        private Double stockTotal;
    }

