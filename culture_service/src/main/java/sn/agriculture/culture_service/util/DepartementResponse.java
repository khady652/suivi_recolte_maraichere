package sn.agriculture.culture_service.util;


import lombok.*;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class DepartementResponse {
        private Integer idDepartement;
        private String nomDepartement;
        private Integer idRegion;
        private Double superficie;
        private String nomRegion;
    }

