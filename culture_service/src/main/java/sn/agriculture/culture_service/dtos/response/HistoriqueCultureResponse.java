package sn.agriculture.culture_service.dtos.response;

import lombok.*;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class HistoriqueCultureResponse {

        private Integer annee;
        private Double surfaceCultivee;
        private String nomTerritoire;
    }

