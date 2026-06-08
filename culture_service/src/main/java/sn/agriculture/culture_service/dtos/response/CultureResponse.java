package sn.agriculture.culture_service.dtos.response;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CultureResponse {

    private Long idCulture;
    private String type;
    private String variete;
    private LocalDate dateSemence;
    private LocalDate datePremierRecoltePrevu;
    private String typeIrrigation;
    private Double quantiteSeme;
    private Double superficiCultive;
    private String saison;
    private Double quantiteRecoltePrevu;
    private String intraUtilise;
    private Boolean fumureOrganique;
    private Long idParcel;
    private String nomParcelle;
    private String lieu;
}