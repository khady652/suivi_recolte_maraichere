package sn.agriculture.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

        // ── LOGIN PAR EMAIL (Directeurs, Admins) ──────────────
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LoginRequest {

            @NotBlank(message = "L'email est obligatoire")
            @Email(message = "Format email invalide")
            private String email;

            @NotBlank(message = "Le mot de passe est obligatoire")
            private String password;
        }

        // ── LOGIN PAR TELEPHONE (Agriculteurs) ────────────────
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LoginTelephoneRequest {

            @NotBlank(message = "Le téléphone est obligatoire")
            private String telephone;

            @NotBlank(message = "Le mot de passe est obligatoire")
            private String password;
        }

        // ── INSCRIPTION ───────────────────────────────────────
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RegisterRequest {

            private String email;
            private String telephone;
            @NotBlank(message = "Le mot de passe est obligatoire")
            @Size(min = 6, message = "Minimum 6 caractères")
            private String password;

            private String role;
            private String nom;
            private String prenom;
            private String adresse;
            private Integer anneeExperience;
            private String niveauInstruction;
            private Integer idCooperative;
        }

        // ── RÉPONSE APRÈS LOGIN RÉUSSI ────────────────────────
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AuthResponse {
            private String accessToken;
            private String tokenType = "Bearer";
            private Long expiresIn;
            private String role;
            private Integer userId;
        }

        // ── RÉPONSE SIMPLE (succès ou erreur) ─────────────────
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MessageResponse {
            private String message;
            private Boolean success;
        }

        // ── REFRESH TOKEN ─────────────────────────────────────
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RefreshTokenRequest {
            @NotBlank(message = "Le refresh token est obligatoire")
            private String refreshToken;
        }
    }

