package sn.agriculteur.marche_service.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

    @Entity
    @Table(name = "collecte_donnees")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class CollecteDonnees {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer idCollecte;

        private LocalDate dateCollecte;
        private String produit;
        private Double prixUnitaire;
        private Double quantiteDisponible;
        private Integer idEnqueteur;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_marche")
        private Marche marche;

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;
    }

