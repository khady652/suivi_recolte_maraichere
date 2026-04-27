package sn.user_service.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Entity
    @Table(name = "decideurs_arm")
    @PrimaryKeyJoinColumn(name = "id_utilisateur")
    @Data


    public class DecideurARM extends Utilisateur{
        // Méthode métier
        public void prendreDecisions() {
            // logique de prise de décisions
        }
    }

