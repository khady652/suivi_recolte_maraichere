package sn.agriculture.geo_service.dtos.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class RegionResponse {
        private Integer idRegion;
        private String nomRegion;
        private Integer population;
        private Double superficie;
        private Double surfaceCultivee;
        private String nomServiceRegionale;
        private Double latitude;
        private Double longitude;
    }

