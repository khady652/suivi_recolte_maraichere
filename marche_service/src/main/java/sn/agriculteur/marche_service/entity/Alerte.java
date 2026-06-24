package sn.agriculteur.marche_service.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String produit;
    private Integer phase;
    private String niveau;
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String recommandation;

    private Double valeurPrincipale;
    private LocalDateTime dateCreation;
}
