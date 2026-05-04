package sn.agriculture.geo_service.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceDepartementaleRequest {
    @NotBlank(message = "Le nom du service est obligatoire")
    private String nomService;

    private String telephoneService;
    private String emailService;
    private String localite;

    @NotNull(message = "Le département est obligatoire")
    private Integer idDepartement;
}
