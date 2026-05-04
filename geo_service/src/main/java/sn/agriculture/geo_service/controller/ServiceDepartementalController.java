package sn.agriculture.geo_service.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.geo_service.dtos.requests.ServiceDepartementaleRequest;
import sn.agriculture.geo_service.dtos.response.MessageResponse;
import sn.agriculture.geo_service.dtos.response.ServiceDepartementaleResponse;
import sn.agriculture.geo_service.service.ServiceDepartementaleService;

import java.util.List;
import java.util.Map;

@RestController
    @RequestMapping("/api/geo/services-departementaux")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class ServiceDepartementalController {

    private final ServiceDepartementaleService serviceDepartementaleService;

    // POST /api/geo/services-departementaux
    @PostMapping
    public ResponseEntity<MessageResponse> creer(
            @Valid @RequestBody ServiceDepartementaleRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(serviceDepartementaleService.creer(request));
    }

    // GET /api/geo/services-departementaux
    @GetMapping
    public ResponseEntity<List<ServiceDepartementaleResponse>> getAll() {
        return ResponseEntity.ok(
                serviceDepartementaleService.getAll());
    }

    // ✅ AVANT /{id} — endpoints statiques
    // GET /api/geo/services-departementaux/ma-region
    @GetMapping("/ma-region")
    public ResponseEntity<List<ServiceDepartementaleResponse>> getMaRegion(
            Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(
                serviceDepartementaleService.getByDirecteurDR(userId));
    }

    // GET /api/geo/services-departementaux/par-region/{idRegion}
    @GetMapping("/par-region/{idRegion}")
    public ResponseEntity<List<ServiceDepartementaleResponse>> getServicesByRegion(
            @PathVariable Integer idRegion) {
        return ResponseEntity.ok(
                serviceDepartementaleService.getByRegion(idRegion));
    }

    // GET /api/geo/services-departementaux/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ServiceDepartementaleResponse> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(
                serviceDepartementaleService.getById(id));
    }

    // PUT /api/geo/services-departementaux/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody ServiceDepartementaleRequest request) {
        return ResponseEntity.ok(
                serviceDepartementaleService.update(id, request));
    }

    // DELETE /api/geo/services-departementaux/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(
            @PathVariable Integer id) {
        return ResponseEntity.ok(
                serviceDepartementaleService.delete(id));
    }

    // PATCH /api/geo/services-departementaux/{id}/affecter-directeur
    @PatchMapping("/{id}/affecter-directeur")
    public ResponseEntity<MessageResponse> affecterDirecteurSDDR(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(
                serviceDepartementaleService.affecterDirecteurSDDR(
                        id, body.get("idDirecteurSddr")));
    }
}

