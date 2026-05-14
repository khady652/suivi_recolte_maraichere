package sn.agriculteur.public_service.Response;



import lombok.*;
import java.util.List;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ZoneProduction {

        private String type = "FeatureCollection";
        private List<Feature> features;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Feature {
            private String type = "Feature";
            private Geometry geometry;
            private Properties properties;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Geometry {
            private String type = "Point";
            private double[] coordinates; // [longitude, latitude]
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Properties {
            private Integer idRegion;
            private String nomRegion;
            private Double production;
            private Double surfaceCultivee;
            private Double superficie;
            private Integer population;
        }
    }

