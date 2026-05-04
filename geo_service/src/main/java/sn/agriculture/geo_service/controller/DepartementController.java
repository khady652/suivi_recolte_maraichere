package sn.agriculture.geo_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.geo_service.dtos.requests.DepartementRequests;
import sn.agriculture.geo_service.dtos.response.DepartementResponse;
import sn.agriculture.geo_service.dtos.response.MessageResponse;
import sn.agriculture.geo_service.dtos.response.ServiceDepartementaleResponse;
import sn.agriculture.geo_service.service.DepartementService;

import java.util.List;

    @RestController
    @RequestMapping("/api/geo/departements")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class DepartementController {

        private final DepartementService departementService;

        // POST /api/geo/departements
        @PostMapping
        public ResponseEntity<MessageResponse> creer(
                @Valid @RequestBody DepartementRequests request) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(departementService.creer(request));
        }

        // GET /api/geo/departements
        @GetMapping
        public ResponseEntity<List<DepartementResponse>> getAll() {
            return ResponseEntity.ok(departementService.getAll());
        }

        // GET /api/geo/departements/{id}
        @GetMapping("/{id}")
        public ResponseEntity<DepartementResponse> getById(
                @PathVariable Integer id) {
            return ResponseEntity.ok(departementService.getById(id));
        }

        // GET /api/geo/departements/region/{idRegion}
        @GetMapping("/region/{idRegion}")
        public ResponseEntity<List<DepartementResponse>> getByRegion(
                @PathVariable Integer idRegion) {
            return ResponseEntity.ok(
                    departementService.getByRegion(idRegion));
        }

        // PUT /api/geo/departements/{id}
        @PutMapping("/{id}")
        public ResponseEntity<MessageResponse> update(
                @PathVariable Integer id,
                @Valid @RequestBody DepartementRequests request) {
            return ResponseEntity.ok(
                    departementService.update(id, request));
        }

        // DELETE /api/geo/departements/{id}
        @DeleteMapping("/{id}")
        public ResponseEntity<MessageResponse> delete(
                @PathVariable Integer id) {
            return ResponseEntity.ok(departementService.delete(id));
        }

        //trouver les departement d une region
        // GET /api/geo/services-departementaux/par-region/{idRegion}
    }


