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
    @Table(name = "regions")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Region {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_region")
        private Integer idRegion;

        @Column(name = "nom_region", nullable = false, unique = true)
        private String nomRegion;

        private Integer population;
        private String superficie;

        @Transient
        private Double surfaceCultivee;

        @OneToOne(mappedBy = "region", cascade = CascadeType.ALL)
        private ServiceRegionale serviceRegionale;

        @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
        private List<Departement> departements;

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;
    }

