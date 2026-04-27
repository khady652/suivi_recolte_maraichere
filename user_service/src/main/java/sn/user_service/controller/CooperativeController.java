package sn.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.user_service.dto.Requests.CooperatifRequest;
import sn.user_service.dto.Responses.CooperativeReponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.service.CooperatifService;

import java.util.List;

@RestController
@RequestMapping("/api/users/cooperatives")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CooperativeController {

    private final CooperatifService cooperativeService;


    @PostMapping
    public ResponseEntity<MessageResponse> creer(
            @Valid @RequestBody CooperatifRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cooperativeService.creer(request));
    }


    @GetMapping
    public ResponseEntity<List<CooperativeReponse>> getAll() {
        return ResponseEntity.ok(cooperativeService.getAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<CooperativeReponse> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(cooperativeService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CooperatifRequest request) {
        return ResponseEntity.ok(
                cooperativeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(
            @PathVariable Integer id) {
        return ResponseEntity.ok(
                cooperativeService.delete(id));
    }
}