package sn.agriculteur.marche_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class CollecteResponse {

        private Integer idCollecte;
        private LocalDate dateCollecte;
        private String produit;
        private Double prixUnitaire;
        private Double quantiteDisponible;
        private Integer idMarche;
        private String nomMarche;
        private String lieuMarche;
        private Integer idEnqueteur;
        private String nomEnqueteur;
        private String prenomEnqueteur;
        private String organisation; // ✅ Ajout
        private String zoneAffectation;
}
