package sn.agriculteur.public_service.Response;

import lombok.*;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class RegionResponse {
        private Integer idRegion;
        private String nomRegion;
        private Integer population;
        private Double superficie;
        private Double latitude;
        private Double longitude;
    }

