package sn.agriculture.geo_service.dtos.requests;



import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class RegionRequest {

        @NotBlank(message = "Le nom de la région est obligatoire")
        private String nomRegion;

        private Integer population;
        private String superficie;
    }

