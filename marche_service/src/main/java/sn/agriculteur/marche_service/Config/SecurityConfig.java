package sn.agriculteur.marche_service.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    @Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(
                HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(
                                    SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth

                            // ── Swagger ───────────────────────────────────
                            .requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/webjars/**"
                            ).permitAll()

                            // ── Actuator + Error ──────────────────────────
                            .requestMatchers("/actuator/health").permitAll()
                            .requestMatchers("/error").permitAll()

                            // ── PUBLIQUES (use case : voir prix / stock) ───
                            .requestMatchers(HttpMethod.GET,
                                    "/api/marche/marches",
                                    "/api/marche/marches/**",
                                    "/api/marche/collectes/derniers-prix",
                                    "/api/marche/collectes/stats/prix-moyen",
                                    "/api/marche/collectes/stats/stock-du-jour",
                                    "/api/marche/collectes/produit/**",
                                    "/api/marche/collectes/stats/**"
                            ).permitAll()

                            // ── ENQUÊTEUR → enregistrer / éditer collectes ─
                            .requestMatchers(HttpMethod.POST,
                                    "/api/marche/collectes"
                            ).hasRole("ENQUETEUR_MARCHE")

                            .requestMatchers(HttpMethod.PUT,
                                    "/api/marche/collectes/**"
                            ).hasRole("ENQUETEUR_MARCHE")

                            .requestMatchers(HttpMethod.GET,
                                    "/api/marche/collectes/mes-collectes"
                            ).hasRole("ENQUETEUR_MARCHE")

                            // ── DÉCIDEUR ARM → gère les marchés ───────────
                            .requestMatchers(HttpMethod.POST,
                                    "/api/marche/marches"
                            ).hasRole("DECIDEUR_ARM")
                            .requestMatchers(HttpMethod.GET,
                                    "/api/marche/stock-alert/**"
                            ).hasRole("DECIDEUR_ARM")
                            .requestMatchers(HttpMethod.PUT,
                                    "/api/marche/marches/**"
                            ).hasRole("DECIDEUR_ARM")

                            .requestMatchers(HttpMethod.DELETE,
                                    "/api/marche/marches/**",
                                    "/api/marche/collectes/**"
                            ).hasRole("DECIDEUR_ARM")

                            // ── DÉCIDEUR ARM + ENQUÊTEUR → voir collectes ──
                            .requestMatchers(HttpMethod.GET,
                                    "/api/marche/collectes",
                                    "/api/marche/collectes/marche/**"
                            ).hasAnyRole("DECIDEUR_ARM",
                                    "ENQUETEUR_MARCHE")
                            .requestMatchers(HttpMethod.PATCH,
                                    "/api/marche/collectes/*/stock"
                            ).hasRole("ENQUETEUR_MARCHE")

                            // ── AGRICULTEUR + CHEF COOP → alertes prix ─────
                            .requestMatchers(HttpMethod.GET,
                                    "/api/marche/collectes/alertes/**"
                            ).hasAnyRole("AGRICULTEUR",
                                    "CHEF_COOPERATIF",
                                    "DECIDEUR_ARM")

                            // ── Tout le reste → authentifié ───────────────
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(jwtAuthFilter,
                            UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }
    }