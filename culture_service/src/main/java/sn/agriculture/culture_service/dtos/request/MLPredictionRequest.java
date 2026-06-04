package sn.agriculture.culture_service.dtos.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class MLPredictionRequest {

        @JsonProperty("region")
        private String region;

        @JsonProperty("variete")
        private String variete;

        @JsonProperty("irrigation")
        private String irrigation;

        @JsonProperty("qualite_sol")
        private String qualiteSol;

        @JsonProperty("engrais")
        private String engrais;

        @JsonProperty("experience_annees")
        private Integer experienceAnnees;

        @JsonProperty("precipitations_mm")
        private Double precipitationsMm;

        @JsonProperty("superficie_m2")
        private Double superficieM2;
    }

