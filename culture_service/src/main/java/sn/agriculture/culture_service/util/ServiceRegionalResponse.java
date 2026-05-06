package sn.agriculture.culture_service.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ServiceRegionalResponse {
        private Integer idService;
        private String nomService;
        private String localite;
        private Integer idRegion; // ✅ Important pour récupérer la région
    }

