package sn.agriculture.geo_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

    @Entity
    @Table(name = "services_departementaux")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ServiceDepartementale {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_service")
        private Integer idService;

        @Column(name = "nom_service", nullable = false)
        private String nomService;

        @Column(name = "telephone_service")
        private String telephoneService;

        @Column(name = "email_service")
        private String emailService;

        private String localite;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_departement", nullable = false)
        private Departement departement;
        @Column(name = "id_directeur_sddr")
        private Integer idDirecteurSddr;
        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;
    }

