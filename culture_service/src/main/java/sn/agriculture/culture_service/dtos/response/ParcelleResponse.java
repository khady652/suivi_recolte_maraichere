package sn.agriculture.culture_service.dtos.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelleResponse {

    private Long idParcel;
    private String nomParcelle;
    private String lieu;
    private Double superficie;
    private String typeSol;
    private String qualiteSol;
    private String sourceEau;
    private Boolean estIrriguee;
    private Long idAgriculteur;
    private Long idDepartement;
    private String nomAgriculteur;
    private String prenomAgriculteur;
    private String nomDepartement;
}