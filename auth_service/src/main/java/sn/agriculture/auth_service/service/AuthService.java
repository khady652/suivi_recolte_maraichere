package sn.agriculture.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.auth_service.dto.AuthDto.*;
import sn.agriculture.auth_service.entity.User;
import sn.agriculture.auth_service.exception.AuthException;
import sn.agriculture.auth_service.repository.UserRepository;
import sn.agriculture.auth_service.security.JwtService;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

        // ── LOGIN PAR EMAIL ───────────────────────────────────
        @Transactional
        public AuthResponse loginByEmail(LoginRequest request) {

            // 1. Chercher l'utilisateur par email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AuthException("Email ou mot de passe incorrect"));

            // 2. Vérifier le mot de passe
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new AuthException("Email ou mot de passe incorrect");
            }

            // 3. Vérifier que le compte est actif
            if (!user.getActif()) {
                throw new AuthException("Votre compte est désactivé. Contactez l'administrateur.");
            }

            // 4. Générer le token JWT
            return buildAuthResponse(user);
        }

        // ── LOGIN PAR TELEPHONE ───────────────────────────────
        @Transactional
        public AuthResponse loginByTelephone(LoginTelephoneRequest request) {

            // 1. Chercher l'utilisateur par téléphone
            User user = userRepository.findByTelephone(request.getTelephone())
                    .orElseThrow(() -> new AuthException("Téléphone ou mot de passe incorrect"));

            // 2. Vérifier le mot de passe
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new AuthException("Téléphone ou mot de passe incorrect");
            }

            // 3. Vérifier que le compte est actif
            if (!user.getActif()) {
                throw new AuthException("Votre compte est désactivé.");
            }

            // 4. Générer le token JWT
            return buildAuthResponse(user);
        }

        // ── INSCRIPTION ───────────────────────────────────────
        @Transactional
        public MessageResponse register(RegisterRequest request) {

            // 1. Vérifier que l'email n'existe pas déjà
            if (request.getEmail() != null &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new AuthException("Cet email est déjà utilisé");
            }

            // 2. Vérifier que le téléphone n'existe pas déjà
            if (request.getTelephone() != null &&
                    userRepository.existsByTelephone(request.getTelephone())) {
                throw new AuthException("Ce numéro est déjà utilisé");
            }

            // 3. Créer l'utilisateur
            User user = new User();
            user.setEmail(request.getEmail());
            user.setTelephone(request.getTelephone());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole() != null ? request.getRole() : "AGRICULTEUR");
            user.setActif(false);

            userRepository.save(user);
            log.info("Nouveau compte créé : {}", user.getEmail());

            return new MessageResponse(
                    "Compte créé avec succès. En attente d'activation.",
                    true
            );
        }

        // ── REFRESH TOKEN ─────────────────────────────────────
        public AuthResponse refreshToken(RefreshTokenRequest request) {

            String refreshToken = request.getRefreshToken();

            // 1. Valider le refresh token
            if (!jwtService.isTokenValid(refreshToken)) {
                throw new AuthException("Refresh token invalide. Reconnectez-vous.");
            }

            // 2. Extraire l'ID utilisateur
            Integer userId = jwtService.extractUserId(refreshToken);

            // 3. Récupérer l'utilisateur
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

            // 4. Générer un nouveau token
            return buildAuthResponse(user);
        }

        // ── MÉTHODE UTILITAIRE ────────────────────────────────
        private AuthResponse buildAuthResponse(User user) {
            String accessToken = jwtService.generateAccessToken(
                    user.getId(), user.getEmail(), user.getRole()
            );

            AuthResponse response = new AuthResponse();
            response.setAccessToken(accessToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(86400L);
            response.setRole(user.getRole());
            response.setUserId(user.getId());

            return response;
        }
    }

