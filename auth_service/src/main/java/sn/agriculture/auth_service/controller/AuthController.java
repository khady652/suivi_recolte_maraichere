package sn.agriculture.auth_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.auth_service.dto.AuthDto.*;
import sn.agriculture.auth_service.dto.CreateAccountRequest;
import sn.agriculture.auth_service.service.AuthService;

    @RestController
    @RequestMapping("/api/auth")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class AuthController {

        private final AuthService authService;

        // ── POST /api/auth/login ──────────────────────────────
        // Login par email (Directeurs, Admins)
        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(
                @Valid @RequestBody LoginRequest request) {
            log.info("Tentative connexion : {}", request.getEmail());
            return ResponseEntity.ok(authService.loginByEmail(request));
        }

        // ── POST /api/auth/login/telephone ────────────────────
        // Login par téléphone (Agriculteurs)
        @PostMapping("/login/telephone")
        public ResponseEntity<AuthResponse> loginByTelephone(
                @Valid @RequestBody LoginTelephoneRequest request) {
            log.info("Connexion téléphone : {}", request.getTelephone());
            return ResponseEntity.ok(authService.loginByTelephone(request));
        }

        // ── POST /api/auth/register ───────────────────────────
        // Inscription
        @PostMapping("/register")
        public ResponseEntity<MessageResponse> register(
                @Valid @RequestBody RegisterRequest request) {
            log.info("Inscription : {}", request.getEmail());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(authService.register(request));
        }

        // ── POST /api/auth/refresh ────────────────────────────
        // Renouveler le token
        @PostMapping("/refresh")
        public ResponseEntity<AuthResponse> refresh(
                @Valid @RequestBody RefreshTokenRequest request) {
            return ResponseEntity.ok(authService.refreshToken(request));
        }

        // ── GET /api/auth/health ──────────────────────────────
        // Vérifier que le service fonctionne
        @GetMapping("/health")
        public ResponseEntity<MessageResponse> health() {
            return ResponseEntity.ok(
                    new MessageResponse("auth-service opérationnel", true,null)
            );
        }
        //ajout d  un agricullteur
        @PostMapping("/internal/create-account")
        public ResponseEntity<MessageResponse> createAccount(
                @RequestBody CreateAccountRequest request) {
            return ResponseEntity.ok(
                    authService.createAccount(request));
        }
    }

