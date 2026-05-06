package sn.agriculture.culture_service.dtos.response;

import lombok.*;
import java.time.LocalDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class RecolteResponse {
        private Long idRecolte;
        private LocalDate dateRecolte;
        private Double quantiteRecolte;
        private Long idCulture;
    }

