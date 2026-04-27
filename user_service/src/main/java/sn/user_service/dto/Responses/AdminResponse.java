package sn.user_service.dto.Responses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class AdminResponse {

        private Integer idUtilisateur;
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private Boolean actif;
    }

