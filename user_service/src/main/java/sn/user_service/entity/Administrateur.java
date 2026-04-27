package sn.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Entity
    @Table(name = "administrateurs")
    @PrimaryKeyJoinColumn(name = "id_utilisateur")
    @Data

    public class Administrateur extends Utilisateur {

        // Méthodes métier
        public void gererUtilisateurs() {
            // logique de gestion des utilisateurs
        }

        public void activerCompte() {
            // logique d'activation de compte
        }

        public void desactiverCompte() {
            // logique de désactivation de compte
        }
    }

