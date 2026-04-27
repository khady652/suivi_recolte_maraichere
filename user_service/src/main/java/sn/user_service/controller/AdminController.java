package sn.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.user_service.dto.Requests.AdminRequest;
import sn.user_service.dto.Responses.AdminResponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.service.AdminService;

import java.util.List;

    @RestController
    @RequestMapping("/api/users/administrateurs")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class AdminController {

        private final AdminService administrateurService;

        // POST /api/users/administrateurs
        @PostMapping
        public ResponseEntity<MessageResponse> creer(
                @Valid @RequestBody AdminRequest request) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(administrateurService.creer(request));
        }

        // GET /api/users/administrateurs
        @GetMapping
        public ResponseEntity<List<AdminResponse>> getAll() {
            return ResponseEntity.ok(administrateurService.getAll());
        }

        // GET /api/users/administrateurs/{id}
        @GetMapping("/{id}")
        public ResponseEntity<AdminResponse> getById(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    administrateurService.getById(id));
        }

        // PUT /api/users/administrateurs/{id}
        @PutMapping("/{id}")
        public ResponseEntity<MessageResponse> update(
                @PathVariable Integer id,
                @Valid @RequestBody AdminRequest request) {
            return ResponseEntity.ok(
                    administrateurService.update(id, request));
        }

        // PUT /api/users/administrateurs/{id}/activer
        @PutMapping("/{id}/activer")
        public ResponseEntity<MessageResponse> activer(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    administrateurService.activerCompte(id));
        }

        // PUT /api/users/administrateurs/{id}/desactiver
        @PutMapping("/{id}/desactiver")
        public ResponseEntity<MessageResponse> desactiver(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    administrateurService.desactiverCompte(id));
        }

        // DELETE /api/users/administrateurs/{id}
        @DeleteMapping("/{id}")
        public ResponseEntity<MessageResponse> delete(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    administrateurService.delete(id));
        }
        @GetMapping("/mon-profil")
        public ResponseEntity<AdminResponse> getMonProfil(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    administrateurService.getMonProfil(userId));
        }

    }

