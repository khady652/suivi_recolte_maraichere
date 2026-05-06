package sn.agriculture.culture_service.dtos.request;



import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class CultureRequest {

        @NotNull(message = "La variété est obligatoire")
        private String variete;

        @NotNull(message = "La date de semence est obligatoire")
        private LocalDate dateSemence;

        private LocalDate datePremierRecoltePrevu;
        private String typeIrrigation;
        private Double quantiteSeme;
        private String superficiCultive;
        private String saison;
        private Double quantiteRecoltePrevu;
        private String intraUtilise;
        private Boolean intraSuplementaire;

        @NotNull(message = "La parcelle est obligatoire")
        private Long idParcel;
    }

