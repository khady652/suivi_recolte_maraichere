package sn.agriculture.culture_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.culture_service.dtos.request.ParcelleRequest;
import sn.agriculture.culture_service.dtos.response.ParcelleResponse;
import sn.agriculture.culture_service.exception.CultureException;
import sn.agriculture.culture_service.service.ParcelleService;

import java.util.List;

@RestController
@RequestMapping("/api/culture/parcelles")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ParcelleController {

    private final ParcelleService parcelleService;

    // ═══════════════════════════════════════════
    // POST /api/culture/parcelles
    // Agriculteur / Directeur SDDR / DRDR / Chef Coopératif
    // ═══════════════════════════════════════════
    /*@PostMapping
    public ResponseEntity<ParcelleResponse> creer(
            @Valid @RequestBody ParcelleRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(parcelleService.creerParcelle(request));
    }*/

    // ═══════════════════════════════════════════
    // PUT /api/culture/parcelles/{id}
    // Agriculteur / Directeur SDDR / DRDR / Chef Coopératif
    // ═══════════════════════════════════════════
    @PutMapping("/{id}")
    public ResponseEntity<ParcelleResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ParcelleRequest request) {
        return ResponseEntity.ok(
                parcelleService.modifierParcelle(id, request));
    }

    // ═══════════════════════════════════════════
    // DELETE /api/culture/parcelles/{id}
    // Agriculteur / Directeur SDDR / DRDR
    // ═══════════════════════════════════════════
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(
            @PathVariable Long id) {
        parcelleService.supprimerParcelle(id);
        return ResponseEntity.noContent().build();
    }

    // ═══════════════════════════════════════════
    // GET /api/culture/parcelles/mes-parcelles
    // Agriculteur → ses propres parcelles
    // ═══════════════════════════════════════════
    @GetMapping("/mes-parcelles")
    public ResponseEntity<List<ParcelleResponse>> getMesParcelles(
            Authentication authentication) {
        Integer idAgriculteur = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(
                parcelleService.getParcellesByAgriculteur(idAgriculteur.longValue()));
    }

    // ═══════════════════════════════════════════
    // GET /api/culture/parcelles/departement/{idDepartement}
    // Directeur SDDR → parcelles de son département
    // ═══════════════════════════════════════════
    // GET /api/culture/parcelles/mon-departement
    @GetMapping("/mon-departement")
    public ResponseEntity<List<ParcelleResponse>> getMonDepartement(
            Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(
                parcelleService.getParcellesByDirecteurSDDR(
                        userId.longValue()));
    }

    // GET /api/culture/parcelles/region/{idRegion}
    // Directeur DRDR → parcelles de sa région

    @GetMapping("/region/{idRegion}")
    public ResponseEntity<List<ParcelleResponse>> getByRegion(
            @PathVariable Integer idRegion) {
        return ResponseEntity.ok(
                parcelleService.getParcellesByRegion(idRegion));
    }

    // GET /api/culture/parcelles/cooperative
    // Chef Coopératif → parcelles de ses membres
    @GetMapping("/ma-cooperative")
    public ResponseEntity<List<ParcelleResponse>> getByCooperative(
            Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(
                parcelleService.getParcellesByChef(userId.longValue()));
    }
    // GET /api/culture/parcelles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ParcelleResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                parcelleService.getParcelleById(id));
    }

    @PostMapping
    public ResponseEntity<ParcelleResponse> creer(
            @Valid @RequestBody ParcelleRequest request,
            Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();
        String role = authentication.getAuthorities()
                .iterator().next().getAuthority()
                .replace("ROLE_", "");


        if (role.equals("AGRICULTEUR")) {
            request.setIdAgriculteur(userId.longValue());
        }

        else if (request.getIdAgriculteur() == null) {
            throw new CultureException(
                    "L'id de l'agriculteur est obligatoire !");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(parcelleService.creerParcelle(request));
    }

    //le directeur regional
    @GetMapping("/ma-region")
    public ResponseEntity<List<ParcelleResponse>> getMaRegion(
            Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(
                parcelleService.getParcellesByDirecteurDR(
                        userId.longValue()));
    }
}