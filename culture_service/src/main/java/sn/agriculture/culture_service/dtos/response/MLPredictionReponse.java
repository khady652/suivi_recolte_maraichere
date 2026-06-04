package sn.agriculture.culture_service.dtos.response;




import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class MLPredictionReponse{

        @JsonProperty("success")
        private Boolean success;

        @JsonProperty("rendement_kg_ha")
        private Double rendementKgHa;

        @JsonProperty("quantiteRecoltePrevu")
        private Double quantiteRecoltePrevu;

        @JsonProperty("superficie_ha")
        private Double superficieHa;

        @JsonProperty("irrigation_mappee")
        private String irrigationMappee;

        @JsonProperty("erreur")
        private String erreur;
    }

