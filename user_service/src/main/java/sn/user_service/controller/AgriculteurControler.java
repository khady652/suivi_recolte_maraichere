package sn.user_service.controller;




import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.user_service.dto.Requests.AgriculteurRequest;
import sn.user_service.dto.Responses.AgriculteurReponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.service.AgiculteurService;

import java.util.List;

    @RestController
    @RequestMapping("/api/users/agriculteurs")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class AgriculteurControler {

        private final AgiculteurService agriculteurService;

        // POST /api/users/agriculteurs
        @PostMapping
        public ResponseEntity<MessageResponse> creer(
                @Valid @RequestBody AgriculteurRequest request) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(agriculteurService.creer(request));
        }

        // GET /api/users/agriculteurs
        @GetMapping
        public ResponseEntity<List<AgriculteurReponse>> getAll() {
            return ResponseEntity.ok(agriculteurService.getAll());
        }

        // GET /api/users/agriculteurs/{id}
        @GetMapping("/{id}")
        public ResponseEntity<AgriculteurReponse> getById(
                @PathVariable Integer id) {
            return ResponseEntity.ok(agriculteurService.getById(id));
        }

        // PUT /api/users/agriculteurs/{id}
        @PutMapping("/{id}")
        public ResponseEntity<MessageResponse> update(
                @PathVariable Integer id,
                @Valid @RequestBody AgriculteurRequest request) {
            return ResponseEntity.ok(agriculteurService.update(id, request));
        }

        // DELETE /api/users/agriculteurs/{id}
        @DeleteMapping("/{id}")
        public ResponseEntity<MessageResponse> delete(
                @PathVariable Integer id) {
            return ResponseEntity.ok(agriculteurService.delete(id));
        }
        @GetMapping("/mon-profil")
        public ResponseEntity<AgriculteurReponse> getMonProfil(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    agriculteurService.getMonProfil(userId));
        }
        @GetMapping("/mes-agriculteurs")
        public ResponseEntity<List<AgriculteurReponse>> getMesAgriculteurs(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            String role = authentication.getAuthorities()
                    .iterator().next().getAuthority()
                    .replace("ROLE_", "");
            return ResponseEntity.ok(
                    agriculteurService.getAgriculteursByRole(userId, role));
        }
    }

