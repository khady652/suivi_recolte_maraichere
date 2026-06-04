package sn.agriculture.culture_service.dtos.response;

import lombok.*;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ProductionMensuelReponse {
        private String mois;
        private Integer moisNum;
        private Integer annee;
        private Double surfaceCultivee;
        private Double productionKg;
        private Integer nombreCultures;
        private Integer nombreRecoltes;
    }

