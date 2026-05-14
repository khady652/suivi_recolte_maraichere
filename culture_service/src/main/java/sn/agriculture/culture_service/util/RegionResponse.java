package sn.agriculture.culture_service.util;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class RegionResponse {
        private Integer idRegion;
        private String nomRegion;
        private Double superficie;
    }

