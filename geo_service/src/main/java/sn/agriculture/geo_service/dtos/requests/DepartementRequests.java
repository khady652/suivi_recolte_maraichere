package sn.agriculture.geo_service.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class DepartementRequests {

        @NotBlank(message = "Le nom du département est obligatoire")
        private String nomDepartement;

        private Integer population;
        private String superficie;

        @NotNull(message = "La région est obligatoire")
        private Integer idRegion;
    }

