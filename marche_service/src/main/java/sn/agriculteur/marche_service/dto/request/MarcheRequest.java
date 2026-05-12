package sn.agriculteur.marche_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class MarcheRequest {

        @NotBlank(message = "Le nom du marché est obligatoire")
        private String nomMarche;

        private String type;
        private String lieu;
    }

