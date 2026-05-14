package sn.agriculteur.public_service.Response;
import lombok.*;
import java.time.LocalDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class CollecteResponse {
        private String produit;
        private Double prixUnitaire;
        private Double quantiteDisponible;
        private String nomMarche;
        private String lieuMarche;
        private LocalDate dateCollecte;
    }

