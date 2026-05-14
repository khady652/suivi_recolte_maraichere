package sn.agriculteur.marche_service.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.agriculteur.marche_service.dto.response.StockAlertResponse;

import sn.agriculteur.marche_service.service.StokAlertService;

@RestController
    @RequestMapping("/api/marche/stock-alert")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class StockAlertController {

        private final StokAlertService stockAlertService;

        @GetMapping("/{produit}")
        public ResponseEntity<StockAlertResponse> getStockAlert(
                @PathVariable String produit) {
            return ResponseEntity.ok(
                    stockAlertService.calculerStockAlert(produit));
        }
    }

