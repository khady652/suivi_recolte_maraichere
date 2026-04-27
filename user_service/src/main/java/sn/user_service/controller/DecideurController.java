package sn.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.user_service.dto.Requests.DecideurRequest;
import sn.user_service.dto.Responses.DecideurResponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.service.DecideurService;

import java.util.List;

    @RestController
    @RequestMapping("/api/users/decideurs")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class DecideurController {

        private final DecideurService decideurARMService;

        // POST /api/users/decideurs
        @PostMapping
        public ResponseEntity<MessageResponse> creer(
                @Valid @RequestBody DecideurRequest request) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(decideurARMService.creer(request));
        }

        // GET /api/users/decideurs
        @GetMapping
        public ResponseEntity<List<DecideurResponse>> getAll() {
            return ResponseEntity.ok(decideurARMService.getAll());
        }

        // GET /api/users/decideurs/{id}
        @GetMapping("/{id}")
        public ResponseEntity<DecideurResponse> getById(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    decideurARMService.getById(id));
        }

        // PUT /api/users/decideurs/{id}
        @PutMapping("/{id}")
        public ResponseEntity<MessageResponse> update(
                @PathVariable Integer id,
                @Valid @RequestBody DecideurRequest request) {
            return ResponseEntity.ok(
                    decideurARMService.update(id, request));
        }

        // DELETE /api/users/decideurs/{id}
        @DeleteMapping("/{id}")
        public ResponseEntity<MessageResponse> delete(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    decideurARMService.delete(id));
        }
        @GetMapping("/mon-profil")
        public ResponseEntity<DecideurResponse> getMonProfil(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    decideurARMService.getMonProfil(userId));
        }
    }

