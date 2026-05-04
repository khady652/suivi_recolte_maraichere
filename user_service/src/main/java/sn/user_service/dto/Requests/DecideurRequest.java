package sn.user_service.dto.Requests;




import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class DecideurRequest {


        @NotBlank(message = "Le nom est obligatoire")
        private String nom;

        @NotBlank(message = "Le prénom est obligatoire")
        private String prenom;

        private String adresse;

        @Email(message = "Format email invalide")
        private String email;
        @Pattern(
                regexp = "^(\\+221|00221)?[0-9]{9}$",
                message = "Format téléphone invalide"
        )
        private String telephone;

}
