package sn.agriculteur.marche_service.dto.response;

import lombok.*;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class MarcheResponse {

        private Integer idMarche;
        private String nomMarche;
        private String type;
        private String lieu;
    }

