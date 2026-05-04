package sn.agriculture.geo_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.geo_service.dtos.requests.ServiceRegionaleRequest;
import sn.agriculture.geo_service.dtos.response.MessageResponse;
import sn.agriculture.geo_service.dtos.response.ServiceRegionaleResponse;
import sn.agriculture.geo_service.service.ServiceRegionaleService;

import java.util.List;
import java.util.Map;

@RestController
    @RequestMapping("/api/geo/services-regionaux")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class ServiceRegionalController {

        private final ServiceRegionaleService serviceRegionaleService;

        // POST /api/geo/services-regionaux
        @PostMapping
        public ResponseEntity<MessageResponse> creer(
                @Valid @RequestBody ServiceRegionaleRequest request) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(serviceRegionaleService.creer(request));
        }

        // GET /api/geo/services-regionaux
        @GetMapping
        public ResponseEntity<List<ServiceRegionaleResponse>> getAll() {
            return ResponseEntity.ok(serviceRegionaleService.getAll());
        }

        // GET /api/geo/services-regionaux/{id}
        @GetMapping("/{id}")
        public ResponseEntity<ServiceRegionaleResponse> getById(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    serviceRegionaleService.getById(id));
        }

        // PUT /api/geo/services-regionaux/{id}
        @PutMapping("/{id}")
        public ResponseEntity<MessageResponse> update(
                @PathVariable Integer id,
                @Valid @RequestBody ServiceRegionaleRequest request) {
            return ResponseEntity.ok(
                    serviceRegionaleService.update(id, request));
        }

        // DELETE /api/geo/services-regionaux/{id}
        @DeleteMapping("/{id}")
        public ResponseEntity<MessageResponse> delete(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    serviceRegionaleService.delete(id));
        }
        // PATCH /api/geo/services-regionaux/{id}/affecter-directeur
        @PatchMapping("/{id}/affecter-directeur")
        public ResponseEntity<MessageResponse> affecterDirecteurDR(
                @PathVariable Integer id,
                @RequestBody Map<String, Integer> body) {
            return ResponseEntity.ok(
                    serviceRegionaleService.affecterDirecteurDR(
                            id, body.get("idDirecteurDr")));
        }
    }

