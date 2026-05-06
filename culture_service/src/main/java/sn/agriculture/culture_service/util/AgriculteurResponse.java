package sn.agriculture.culture_service.util;



import lombok.*;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class AgriculteurResponse {

        // Champs hérités de Utilisateur
        private Integer idUtilisateur;
        private String nom;
        private String prenom;
        private String adresse;
        private String email;
        private String telephone;
        private String role;
        private Boolean actif;

        private Integer anneeExperience;
        private String niveauInstruction;
    }

