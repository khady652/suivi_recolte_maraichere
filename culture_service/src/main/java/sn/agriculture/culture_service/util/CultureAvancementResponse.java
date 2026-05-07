package sn.agriculture.culture_service.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class CultureAvancementResponse {

        private String nomRegion;
        private Integer totalCultures;
        private Integer enCours;
        private Integer prete;
        private Integer enRetard;
        private Integer recoltee;
        private Integer planifiee;

        // Pourcentages
        private Double pourcentageEnCours;
        private Double pourcentagePrete;
        private Double pourcentageEnRetard;
        private Double pourcentageRecoltee;
        private Double pourcentagePlanifiee;

        // Avancement moyen de la région
        private Double avancementMoyenPourcent;
    }

