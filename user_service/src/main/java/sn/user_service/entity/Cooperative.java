package sn.user_service.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

    @Entity
    @Table(name = "cooperatives")
    @Data
 @AllArgsConstructor
    @NoArgsConstructor
    public class Cooperative{

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       @Column(name="id_cooperatif")
        private Integer idCooperation;

        @Column(name = "nom_cooperative")
        private String nomCooperative;

        private String adresse;

        @Column(name = "date_creation")
        private LocalDate dateCreation;

        @Column(name = "nombre_membres")
        private Integer nombreMembres;

        // Une coopérative a plusieurs agriculteurs
        @OneToMany(mappedBy = "cooperative", cascade = CascadeType.ALL)
        private List<Agriculteur> agriculteurs;

        // Une coopérative a un seul chef
        @OneToOne(mappedBy = "cooperative")
        private ChefCooperatif chefCooperatif;
    }

