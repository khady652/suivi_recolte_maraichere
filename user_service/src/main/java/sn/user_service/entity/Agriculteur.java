package sn.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agriculteurs")
@PrimaryKeyJoinColumn(name = "id_utilisateur")
@Data

    public class Agriculteur extends Utilisateur {

        @Column(name = "annee_experience")
        private Integer anneeExperience;

        @Column(name = "niveau_instruction")
        private String niveauInstruction;


        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "id_cooperative",nullable = true)
        private Cooperative cooperative;

        public void enregistrerRecolte() {
        }

        public void consulterPrix() {
        }
    }

