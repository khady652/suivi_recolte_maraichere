package sn.agriculture.culture_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "culture")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"parcelle", "recoltes"})
@EqualsAndHashCode(exclude = {"parcelle", "recoltes"})
public class Culture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCulture;

    private String type;
    private String variete;
    private LocalDate dateSemence;
    private LocalDate datePremierRecoltePrevu;
    private String typeIrrigation;
    private Double quantiteSeme;
    private Double superficiCultive;
    private String saison;
    private Double quantiteRecoltePrevu;
    private String intraUtilise;
    private Boolean intraSuplementaire;
    private Boolean engrais;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parcel")
    private Parcelle parcelle;

    @OneToMany(mappedBy = "culture",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Recolte> recoltes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}