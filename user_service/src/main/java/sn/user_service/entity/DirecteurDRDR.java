package sn.user_service.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "directeurs_drdr")
@PrimaryKeyJoinColumn(name = "id_utilisateur")
@Data

public class DirecteurDRDR extends Utilisateur {
    private String specialite;

    // Méthodes métier
    public void genererRapportAgricol() {
        // logique de génération de rapport
    }

    public void visualiserRecolte() {
        // logique de visualisation des récoltes
    }
}

