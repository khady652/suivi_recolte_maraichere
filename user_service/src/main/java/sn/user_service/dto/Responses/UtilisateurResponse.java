package sn.user_service.dto.Responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class UtilisateurResponse {
        private Integer idUtilisateur;
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String role;
        private Boolean actif;

}
