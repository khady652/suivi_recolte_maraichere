package sn.agriculture.culture_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CultureRequest {

    @NotBlank(message = "Le type est obligatoire")
    private String type;

    @NotBlank(message = "La variété est obligatoire")
    private String variete;

    @NotNull(message = "La date de semence est obligatoire")
    private LocalDate dateSemence;
    private String typeIrrigation;
    private Double quantiteSeme;
    private Double superficiCultive;
    private String saison;
    private String intraUtilise;
    private Boolean intraSuplementaire;
    private Boolean engrais;

    @NotNull(message = "La parcelle est obligatoire")
    private Long idParcel;
}