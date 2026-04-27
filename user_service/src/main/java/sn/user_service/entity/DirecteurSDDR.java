package sn.user_service.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Entity
    @Table(name = "directeurs_sddr")
    @PrimaryKeyJoinColumn(name = "id_utilisateur")
    @Data

    public class DirecteurSDDR extends Utilisateur {
        private String specialite;
        @Column(name = "id_service_sddr")
        private Integer idServiceSDDR;
        // Méthodes métier
        public void genererRapportAgricol() {
            // logique de génération de rapport
        }

        public void visualiserRecolte() {
            // logique de visualisation des récoltes
        }
    }

