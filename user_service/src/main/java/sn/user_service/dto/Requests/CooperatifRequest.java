package sn.user_service.dto.Requests;




import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class CooperatifRequest {

        @NotBlank(message = "Le nom est obligatoire")
        private String nomCooperative;

        private String adresse;

        @Min(value = 1, message = "Le nombre de membres doit être supérieur à 0")
        private Integer nombreMembres;
        private LocalDate dateCreation;
    }

