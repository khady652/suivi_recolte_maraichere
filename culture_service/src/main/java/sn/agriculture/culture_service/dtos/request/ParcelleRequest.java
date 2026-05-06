package sn.agriculture.culture_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelleRequest {

    @NotBlank(message = "Le nom de la parcelle est obligatoire")
    private String nomParcelle;

    @NotBlank(message = "Le lieu est obligatoire")
    private String lieu;

    @NotNull(message = "La superficie est obligatoire")
    private Double superficie;

    private String typeSol;
    private String qualiteSol;
    private String sourceEau;
    private Boolean estIrriguee;

    //@NotNull(message = "L'agriculteur est obligatoire")
    private Long idAgriculteur;

    @NotNull(message = "Le département est obligatoire")
    private Long idDepartement;
}