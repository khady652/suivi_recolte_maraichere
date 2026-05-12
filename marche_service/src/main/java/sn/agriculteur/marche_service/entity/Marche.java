package sn.agriculteur.marche_service.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

    @Entity
    @Table(name = "marche")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Marche {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer idMarche;

        @Column(nullable = false)
        private String nomMarche;

        private String type;
        private String lieu;

        @OneToMany(mappedBy = "marche",
                cascade = CascadeType.ALL,
                fetch = FetchType.LAZY)
        private List<CollecteDonnees> collectes;

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;
    }

