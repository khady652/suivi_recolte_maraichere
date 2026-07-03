package sn.user_service.dto.Responses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class CooperativeReponse {

        private Integer idCooperation;
        private String nomCooperative;
        private String adresse;
        private Integer nombreMembres;
        private String nomChef;
        private String prenomChef;
        private String telephoneChef;
        private LocalDate creationDate;
    }

