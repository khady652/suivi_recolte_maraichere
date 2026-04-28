package sn.user_service.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

    @Entity
    @Table(name = "utilisateurs")
    @Inheritance(strategy = InheritanceType.JOINED)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Utilisateur {

        @Id
        @Column(name = "id_utilisateur")
        private Integer idUtilisateur;

        private String nom;
        private String prenom;
        private String adresse;

        @Column(unique = true)
        private String email;

        @Column(unique = true)
        private String telephone;

        private String password;

        private String role;
        private Boolean actif;

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;
    }

