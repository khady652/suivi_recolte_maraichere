package sn.agriculture.geo_service.dtos.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class DepartementResponse {
        private Integer idDepartement;
        private String nomDepartement;
        private Integer population;
        private Double superficie;
        private Double surfaceCultivee;
        private String nomRegion;
        private String nomServiceDepartementale;
        private Double latitude;
        private Double longitude;
    }

