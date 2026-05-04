package sn.user_service.controller;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.user_service.dto.Requests.DirecteurRequest;
import sn.user_service.dto.Responses.DirecteurResponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.service.DirecteurService;

import java.util.List;
import java.util.Map;

@RestController
    @RequestMapping("/api/users/directeurs")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class DirecteurController {

        private final DirecteurService directeurService;

        // POST /api/users/directeurs/dr
        @PostMapping("/dr")
        public ResponseEntity<MessageResponse> creerDR(
                @Valid @RequestBody DirecteurRequest request) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(directeurService.creerDR(request));
        }

        // GET /api/users/directeurs/dr
        @GetMapping("/dr")
        public ResponseEntity<List<DirecteurResponse>> getAllDR() {
            return ResponseEntity.ok(directeurService.getAllDR());
        }

        // GET /api/users/directeurs/dr/{id}
        @GetMapping("/dr/{id}")
        public ResponseEntity<DirecteurResponse> getDRById(
                @PathVariable Integer id) {
            return ResponseEntity.ok(directeurService.getDRById(id));
        }

        // PUT /api/users/directeurs/dr/{id}
        @PutMapping("/dr/{id}")
        public ResponseEntity<MessageResponse> updateDR(
                @PathVariable Integer id,
                @Valid @RequestBody DirecteurRequest request) {
            return ResponseEntity.ok(
                    directeurService.updateDR(id, request));
        }

        // DELETE /api/users/directeurs/dr/{id}
        @DeleteMapping("/dr/{id}")
        public ResponseEntity<MessageResponse> deleteDR(
                @PathVariable Integer id) {
            return ResponseEntity.ok(directeurService.deleteDR(id));
        }

        // ══════════════════════════════════════════════════════
        //  DIRECTEUR SDDR
        // ══════════════════════════════════════════════════════

        // POST /api/users/directeurs/sddr
        @PostMapping("/sddr")
        public ResponseEntity<MessageResponse> creerSDDR(
                @Valid @RequestBody DirecteurRequest request) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(directeurService.creerSDDR(request));
        }

        // GET /api/users/directeurs/sddr
        @GetMapping("/sddr")
        public ResponseEntity<List<DirecteurResponse>> getAllSDDR() {
            return ResponseEntity.ok(directeurService.getAllSDDR());
        }

        // GET /api/users/directeurs/sddr/{id}
        @GetMapping("/sddr/{id}")
        public ResponseEntity<DirecteurResponse> getSDDRById(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    directeurService.getSDDRById(id));
        }

        // PUT /api/users/directeurs/sddr/{id}
        @PutMapping("/sddr/{id}")
        public ResponseEntity<MessageResponse> updateSDDR(
                @PathVariable Integer id,
                @Valid @RequestBody DirecteurRequest request) {
            return ResponseEntity.ok(
                    directeurService.updateSDDR(id, request));
        }

        // DELETE /api/users/directeurs/sddr/{id}
        @DeleteMapping("/sddr/{id}")
        public ResponseEntity<MessageResponse> deleteSDDR(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    directeurService.deleteSDDR(id));
        }
        @GetMapping("/drdr/mon-profil")
        public ResponseEntity<DirecteurResponse> getMonProfilDR(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    directeurService.getMonProfilDR(userId));
        }

        @GetMapping("/sddr/mon-profil")
        public ResponseEntity<DirecteurResponse> getMonProfilSDDR(
                Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(
                    directeurService.getMonProfilSDDR(userId));
        }

        // GET /api/users/directeurs/sddr/{id}/info
        @GetMapping("/sddr/{id}/info")
        public ResponseEntity<Map<String, String>> getSDDRInfo(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    directeurService.getSDDRInfo(id));
        }

        // GET /api/users/directeurs/dr/{id}/info
        @GetMapping("/dr/{id}/info")
        public ResponseEntity<Map<String, Object>> getDRInfo(
                @PathVariable Integer id) {
            return ResponseEntity.ok(
                    directeurService.getDRInfo(id));
        }

    }

