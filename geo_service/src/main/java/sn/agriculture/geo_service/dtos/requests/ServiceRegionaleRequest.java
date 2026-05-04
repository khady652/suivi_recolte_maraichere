package sn.agriculture.geo_service.dtos.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRegionaleRequest {

    @NotBlank(message = "Le nom du service est obligatoire")
    private String nomService;

    private String telephoneService;
    private String emailService;
    private String localite;

    @NotNull(message = "La région est obligatoire")
    private Integer idRegion;
}