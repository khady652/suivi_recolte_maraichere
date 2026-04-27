package sn.user_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Entity
    @Table(name = "enqueteurs_marche")
    @PrimaryKeyJoinColumn(name = "id_utilisateur")
    @Data
    public class EnqueteurMarche extends Utilisateur {
        @Column(name = "organisation")
        private String organisation;

        @Column(name = "zone_affectation")
        private String zoneAffectation;



    }

