package sn.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.user_service.dto.Requests.EnqueteurRequest;
import sn.user_service.dto.Responses.EnqueteurResponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.service.EnqueteurService;

import java.util.List;

@RestController
@RequestMapping("/api/users/enqueteurs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EnqueteurController {

    private final EnqueteurService enqueteurService;

    // POST /api/users/enqueteurs
    @PostMapping
    public ResponseEntity<MessageResponse> creer(
            @Valid @RequestBody EnqueteurRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(enqueteurService.creer(request));
    }

    // GET /api/users/enqueteurs
    @GetMapping
    public ResponseEntity<List<EnqueteurResponse>> getAll() {
        return ResponseEntity.ok(enqueteurService.getAll());
    }

    // GET /api/users/enqueteurs/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EnqueteurResponse> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(
                enqueteurService.getById(id));
    }

    // PUT /api/users/enqueteurs/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody EnqueteurRequest request) {
        return ResponseEntity.ok(
                enqueteurService.update(id, request));
    }

    // DELETE /api/users/enqueteurs/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(
            @PathVariable Integer id) {
        return ResponseEntity.ok(
                enqueteurService.delete(id));
    }
    @GetMapping("/mon-profil")
    public ResponseEntity<EnqueteurResponse> getMonProfil(
            Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(
                enqueteurService.getMonProfil(userId));
    }

}

