package sn.user_service.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.user_service.dto.Requests.ChefCooperatifRequest;
import sn.user_service.dto.Responses.ChefCooperatifResponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.service.ChefCooperativeService;

import java.util.List;

    @RestController
    @RequestMapping("/api/users/chefs-cooperatifs")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class ChefCooperatifController {

        private final ChefCooperativeService chefCooperatifService;

        // POST /api/users/chefs-cooperatifs
        @PostMapping
        public ResponseEntity<MessageResponse> creer(
                @Valid @RequestBody ChefCooperatifRequest request) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(chefCooperatifService.creer(request));
        }

        // GET /api/users/chefs-cooperatifs
        @GetMapping
        public ResponseEntity<List<ChefCooperatifResponse>> getAll() {
            return ResponseEntity.ok(chefCooperatifService.getAll());
        }

        // GET /api/users/chefs-cooperatifs/{id}
        @GetMapping("/{id}")
        public ResponseEntity<ChefCooperatifResponse> getById(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    chefCooperatifService.getById(id));
        }

        // PUT /api/users/chefs-cooperatifs/{id}
        @PutMapping("/{id}")
        public ResponseEntity<MessageResponse> update(
                @PathVariable Integer id,
                @Valid @RequestBody ChefCooperatifRequest request) {
            return ResponseEntity.ok(
                    chefCooperatifService.update(id, request));
        }

        // DELETE /api/users/chefs-cooperatifs/{id}
        @DeleteMapping("/{id}")
        public ResponseEntity<MessageResponse> delete(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    chefCooperatifService.delete(id));
        }

        @GetMapping("/mon-profil")
        public ResponseEntity<ChefCooperatifResponse> getMonProfil(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    chefCooperatifService.getMonProfil(userId));
        }

    }
