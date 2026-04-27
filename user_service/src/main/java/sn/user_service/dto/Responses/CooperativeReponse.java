package sn.user_service.dto.Responses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class CooperativeReponse {

        private Integer idCooperation;
        private String nomCooperative;
        private String adresse;
        private Integer nombreMembres;
        private String nomChef;
    }

