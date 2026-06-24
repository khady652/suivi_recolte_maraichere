package sn.agriculteur.marche_service.dto.response;
import lombok.*;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrevisionClientResponse {

    private String produit;
    private Double productionPrevueTonnes;
    private String periodeRecolte;
    private Map<String, Double> productionParMois;
    private Map<String, Double> productionParRegion;
}
