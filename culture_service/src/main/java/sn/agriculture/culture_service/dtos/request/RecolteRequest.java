package sn.agriculture.culture_service.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class RecolteRequest {

        @NotNull(message = "La date de récolte est obligatoire")
        private LocalDate dateRecolte;

        @NotNull(message = "La quantité est obligatoire")
        private Double quantiteRecolte;

        @NotNull(message = "La culture est obligatoire")
        private Long idCulture;
    }

