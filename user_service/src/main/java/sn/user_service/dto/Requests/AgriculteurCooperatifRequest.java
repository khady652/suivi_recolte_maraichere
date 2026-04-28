package sn.user_service.dto.Requests;

import lombok.Data;

    @Data
    public class AgriculteurCooperatifRequest {
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String adresse;
        private Integer anneeExperience;
        private String niveauInstruction;
    }

