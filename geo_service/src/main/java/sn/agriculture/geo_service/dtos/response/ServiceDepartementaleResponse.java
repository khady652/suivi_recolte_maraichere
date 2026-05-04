package sn.agriculture.geo_service.dtos.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ServiceDepartementaleResponse {
        private Integer idService;
        private String nomService;
        private String telephoneService;
        private String emailService;
        private String localite;
        private String nomDepartement;

        //enplus nom et prenom de son directeur
        private String nomDirecteur;
        private String prenomDirecteur;
    }

