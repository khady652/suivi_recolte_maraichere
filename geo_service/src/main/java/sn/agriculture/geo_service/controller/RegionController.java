package sn.agriculture.geo_service.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.geo_service.dtos.requests.RegionRequest;
import sn.agriculture.geo_service.dtos.response.MessageResponse;
import sn.agriculture.geo_service.dtos.response.RegionResponse;
import sn.agriculture.geo_service.service.RegionService;

import java.util.List;

    @RestController
    @RequestMapping("/api/geo/regions")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class RegionController {

        private final RegionService regionService;

        // POST /api/geo/regions
        @PostMapping
        public ResponseEntity<MessageResponse> creer(
                @Valid @RequestBody RegionRequest request) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(regionService.creer(request));
        }

        // GET /api/geo/regions
        @GetMapping
        public ResponseEntity<List<RegionResponse>> getAll() {
            return ResponseEntity.ok(regionService.getAll());
        }

        // GET /api/geo/regions/{id}
        @GetMapping("/{id}")
        public ResponseEntity<RegionResponse> getById(
                @PathVariable Integer id) {
            return ResponseEntity.ok(regionService.getById(id));
        }

        // PUT /api/geo/regions/{id}
        @PutMapping("/{id}")
        public ResponseEntity<MessageResponse> update(
                @PathVariable Integer id,
                @Valid @RequestBody RegionRequest request) {
            return ResponseEntity.ok(regionService.update(id, request));
        }

        // DELETE /api/geo/regions/{id}
        @DeleteMapping("/{id}")
        public ResponseEntity<MessageResponse> delete(
                @PathVariable Integer id) {
            return ResponseEntity.ok(regionService.delete(id));
        }
    }

