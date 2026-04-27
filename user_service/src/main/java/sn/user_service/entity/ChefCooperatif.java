package sn.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Entity
    @Table(name = "chefs_cooperatifs")
    @PrimaryKeyJoinColumn(name = "id_utilisateur")
    @Data

    public class ChefCooperatif extends Utilisateur {

        @OneToOne
        @JoinColumn(name = "id_cooperation")
        private Cooperative cooperative;

        // Méthode métier
        public void gererMembres() {
            // logique de gestion des membres
        }
    }
