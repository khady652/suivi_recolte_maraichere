package sn.agriculture.geo_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

    @Entity
    @Table(name = "departements")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Departement {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_departement")
        private Integer idDepartement;

        @Column(name = "nom_departement", nullable = false, unique = true)
        private String nomDepartement;

        private Integer population;
        private String superficie;

        @Transient
        private Double surfaceCultivee;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_region", nullable = false)
        private Region region;

        @OneToOne(mappedBy = "departement", cascade = CascadeType.ALL)
        private ServiceDepartementale serviceDepartementale;

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;
    }

