package sn.agriculteur.marche_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class CollecteRequest {

        @NotNull(message = "La date de collecte est obligatoire")
        private LocalDate dateCollecte;

        @NotBlank(message = "Le produit est obligatoire")
        private String produit;

        @NotNull(message = "Le prix unitaire est obligatoire")
        private Double prixUnitaire;

        @NotNull(message = "La quantité disponible est obligatoire")
        private Double quantiteDisponible;

        @NotNull(message = "Le marché est obligatoire")
        private Integer idMarche;

}
