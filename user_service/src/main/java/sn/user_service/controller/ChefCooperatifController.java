package sn.user_service.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.user_service.dto.Requests.AgriculteurCooperatifRequest;
import sn.user_service.dto.Requests.ChefCooperatifRequest;
import sn.user_service.dto.Responses.AgriculteurReponse;
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

        // POST /api/users/chefs-cooperatifs/agriculteurs
        @PostMapping("/agriculteurs")
        public ResponseEntity<MessageResponse> inscrireAgriculteur(
                @Valid @RequestBody AgriculteurCooperatifRequest request,
                Authentication authentication) {
            Integer chefUserId = (Integer) authentication.getPrincipal();
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(chefCooperatifService.inscrireAgriculteur(
                            chefUserId, request));
        }

        // GET /api/users/chefs-cooperatifs/mes-agriculteurs
        @GetMapping("/mes-agriculteurs")
        public ResponseEntity<List<AgriculteurReponse>> getMesAgriculteurs(
                Authentication authentication) {
            Integer chefUserId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    chefCooperatifService.getMesAgriculteurs(chefUserId));
        }

    }
