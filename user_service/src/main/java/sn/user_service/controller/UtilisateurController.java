package sn.user_service.controller;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.user_service.dto.Responses.CooperativeReponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.dto.Responses.UtilisateurResponse;
import sn.user_service.repository.AgriculteurRepo;
import sn.user_service.repository.CooperativeRepository;
import sn.user_service.repository.EnqueteurRepo;
import sn.user_service.service.UtilisateurService;

import java.util.List;
import java.util.Map;

@RestController
    @RequestMapping("/api/users/admin")
    @RequiredArgsConstructor
    @Slf4j
    // @CrossOrigin(origins = "*")
    public class UtilisateurController {

        private final UtilisateurService utilisateurService;
        private final EnqueteurRepo enqueteurRepo;
        private final CooperativeRepository coopRepo;
        private final AgriculteurRepo agriculteurRepo;
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


        // GET /api/users/admin/mon-profil
        @GetMapping("/mon-profil")
        public ResponseEntity<UtilisateurResponse> getMonProfil(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    utilisateurService.getMonProfil(userId));
        }

        @GetMapping("/stats/public")
        public ResponseEntity<Map<String, Long>> getStatsPubliques() {
            return ResponseEntity.ok(Map.of(
                    "nbAgriculteurs", agriculteurRepo.count(),
                    "nbEnqueteurs", enqueteurRepo.count(),
                    "nbCooperatives", coopRepo.count()
            ));
        }
    }

