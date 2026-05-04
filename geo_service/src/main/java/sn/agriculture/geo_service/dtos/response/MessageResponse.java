package sn.agriculture.geo_service.dtos.response;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class MessageResponse {
        private String message;
        private boolean success;
    }

