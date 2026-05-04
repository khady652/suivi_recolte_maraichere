package sn.user_service.controller;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.dto.Responses.UtilisateurResponse;
import sn.user_service.service.UtilisateurService;

import java.util.List;

    @RestController
    @RequestMapping("/api/users/admin")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class UtilisateurController {

        private final UtilisateurService utilisateurService;

        // PATCH /api/users/admin/activer/{userId}
        @PatchMapping("/activer/{userId}")
        public ResponseEntity<MessageResponse> activerCompte(
                @PathVariable Integer userId) {
            return ResponseEntity.ok(
                    utilisateurService.activerCompte(userId));
        }

        // PATCH /api/users/admin/desactiver/{userId}
        @PatchMapping("/desactiver/{userId}")
        public ResponseEntity<MessageResponse> desactiverCompte(
                @PathVariable Integer userId) {
            return ResponseEntity.ok(
                    utilisateurService.desactiverCompte(userId));
        }

        // GET /api/users/admin/utilisateurs
        @GetMapping("/utilisateurs")
        public ResponseEntity<List<UtilisateurResponse>> getAll() {
            return ResponseEntity.ok(
                    utilisateurService.getAll());
        }
    }

