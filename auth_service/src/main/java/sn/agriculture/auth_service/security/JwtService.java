package sn.agriculture.auth_service.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

    @Service
    @Slf4j
    public class JwtService {

        @Value("${jwt.secret}")
        private String jwtSecret;

        @Value("${jwt.expiration}")
        private Long jwtExpiration;

        @Value("${jwt.refresh-expiration}")
        private Long refreshExpiration;

        // ── Générer Access Token (24h) ────────────────────────
        public String generateAccessToken(Integer userId, String email, String role) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("role", role);
            claims.put("type", "ACCESS");

            return buildToken(claims, email != null ? email : userId.toString(), jwtExpiration);
        }

        // ── Générer Refresh Token (7 jours) ──────────────────
        public String generateRefreshToken(Integer userId) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("type", "REFRESH");

            return buildToken(claims, userId.toString(), refreshExpiration);
        }

        // ── Extraire userId du token ──────────────────────────
        public Integer extractUserId(String token) {
            return extractAllClaims(token).get("userId", Integer.class);
        }

        // ── Extraire le rôle du token ─────────────────────────
        public String extractRole(String token) {
            return extractAllClaims(token).get("role", String.class);
        }

        // ── Vérifier si le token est valide ───────────────────
        public boolean isTokenValid(String token) {
            try {
                extractAllClaims(token);
                return true;
            } catch (JwtException | IllegalArgumentException e) {
                log.warn("Token JWT invalide : {}", e.getMessage());
                return false;
            }
        }

        // ── Vérifier si c'est un access token ─────────────────
        public boolean isAccessToken(String token) {
            String type = extractAllClaims(token).get("type", String.class);
            return "ACCESS".equals(type);
        }

        // ── Méthodes privées ──────────────────────────────────
        private String buildToken(Map<String, Object> claims, String subject, Long expiration) {
            return Jwts.builder()
                    .claims(claims)
                    .subject(subject)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey())
                    .compact();
        }

        private Claims extractAllClaims(String token) {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }

        private SecretKey getSigningKey() {
            byte[] keyBytes = Decoders.BASE64.decode(
                    java.util.Base64.getEncoder()
                            .encodeToString(jwtSecret.getBytes())
            );
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }

