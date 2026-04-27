package sn.user_service.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class AgriculteurReponse {

        private Integer idUtilisateur;
        private String nom;
        private String prenom;
        private String adresse;
        private String email;
        private String telephone;
        private Integer anneeExperience;
        private String niveauInstruction;
        private Boolean actif;
        private String nomCooperative;
    }

