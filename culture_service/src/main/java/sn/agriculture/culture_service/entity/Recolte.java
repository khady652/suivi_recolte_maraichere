package sn.agriculture.culture_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recolte")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recolte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecolte;

    private LocalDate dateRecolte;
    private Double quantiteRecolte;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_culture")
    private Culture culture;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}