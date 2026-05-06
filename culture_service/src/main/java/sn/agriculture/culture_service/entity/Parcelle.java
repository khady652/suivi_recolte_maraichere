package sn.agriculture.culture_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "parcelle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcelle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParcel;

    private String nomParcelle;
    private String lieu;
    private Double superficie;
    private String typeSol;
    private String qualiteSol; // ✅ Ajout pour ML
    private String sourceEau;
    private Boolean estIrriguee;
    private Long idAgriculteur;
    private Long idDepartement;

    @OneToMany(mappedBy = "parcelle",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Culture> cultures;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}