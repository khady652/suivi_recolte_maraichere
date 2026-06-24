package sn.agriculteur.marche_service.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerte_lecture")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlerteLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_alerte")
    private Alerte alerte;

    private Integer idDecideur;
    private LocalDateTime dateLecture;
}