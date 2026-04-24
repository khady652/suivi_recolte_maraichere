package sn.agriculture.auth_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

    @Component
    @RequiredArgsConstructor
    @Slf4j

    public class JwtAuthFilter extends OncePerRequestFilter {

        private final JwtService jwtService;

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {

            // 1. Récupérer le header Authorization
            String authHeader = request.getHeader("Authorization");

            // 2. Si pas de token → continuer sans authentification
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 3. Extraire le token (enlever "Bearer ")
            String token = authHeader.substring(7);

            try {
                // 4. Valider le token
                if (jwtService.isTokenValid(token) && jwtService.isAccessToken(token)) {

                    Integer userId = jwtService.extractUserId(token);
                    String role    = jwtService.extractRole(token);

                    // 5. Créer l'authentification Spring Security
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            );

                    // 6. Enregistrer dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Utilisateur {} authentifié avec rôle {}", userId, role);
                }
            } catch (Exception e) {
                log.warn("Erreur validation JWT : {}", e.getMessage());
            }

            filterChain.doFilter(request, response);
        }
    }

