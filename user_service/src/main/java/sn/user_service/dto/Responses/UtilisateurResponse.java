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
        private String adresse;

        private Integer anneeExperience;
        private String niveauInstruction;
        private String nomCooperative;
        private Integer idCooperative;
        private String organisation;
        private String zoneAffectation;
        private String specialite;
        private String dateCreation;
}
