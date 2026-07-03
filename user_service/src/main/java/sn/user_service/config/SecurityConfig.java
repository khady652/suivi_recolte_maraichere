package sn.user_service.config;

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

    private final JwtAthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── Swagger ───────────────────────────────────
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()

                        // ── Actuator ──────────────────────────────────
                        .requestMatchers("/actuator/health").permitAll()

                        // ── Endpoints internes (autres microservices) ─
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/agriculteurs/*/info",
                                "/api/users/agriculteurs/*/chef-info",
                                "/api/users/directeurs/sddr/*/info",
                                "/api/users/directeurs/dr/*/info",
                                "/api/users/decideurs",
                                "/api/users/enqueteurs/*/info"
                        ).permitAll()

                        // ── ROUTES PUBLIQUES ──────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/cooperatives",
                                "/api/users/cooperatives/**"
                        ).permitAll()

                        // ── STATS PUBLIQUES ───────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/stats/public",
                                "/api/users/admin/stats/public"
                        ).permitAll()

                        // ── MON PROFIL — AGRICULTEUR ──────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/agriculteurs/mon-profil"
                        ).hasRole("AGRICULTEUR")

                        // ── CHEF COOPERATIF — GET ─────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/chefs-cooperatifs/mon-profil",
                                "/api/users/chefs-cooperatifs/mes-agriculteurs"
                        ).hasRole("CHEF_COOPERATIF")

                        .requestMatchers(HttpMethod.GET,
                                "/api/users/chefs-cooperatifs/**"
                        ).hasAnyRole("ADMINISTRATEUR", "CHEF_COOPERATIF")

                        // ── CHEF COOPERATIF — POST ────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/users/chefs-cooperatifs/agriculteurs"
                        ).hasRole("CHEF_COOPERATIF")

                        .requestMatchers(HttpMethod.POST,
                                "/api/users/cooperatives"
                        ).hasAnyRole("ADMINISTRATEUR", "CHEF_COOPERATIF")
                        // ── CHEF COOPERATIF — PUT ─────────────────────
                        .requestMatchers(HttpMethod.PUT,
                                "/api/users/agriculteurs/**"
                        ).hasAnyRole("ADMINISTRATEUR", "AGRICULTEUR", "CHEF_COOPERATIF")

                        // ── CHEF COOPERATIF — DELETE ──────────────────
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/users/agriculteurs/**"
                        ).hasAnyRole("ADMINISTRATEUR", "CHEF_COOPERATIF")

                        // ── MON PROFIL — DIRECTEUR DR ─────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/directeurs/drdr/mon-profil",
                                "/api/users/directeurs/dr/mon-profil"
                        ).hasRole("DIRECTEUR_DR")

                        // ── MON PROFIL — DIRECTEUR SDDR ───────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/directeurs/sddr/mon-profil"
                        ).hasRole("DIRECTEUR_SDDR")

                        // ── MON PROFIL — DECIDEUR ARM ─────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/decideurs/mon-profil"
                        ).hasRole("DECIDEUR_ARM")
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/enqueteurs/mon-profil"
                        ).hasRole("ENQUETEUR_MARCHE")
                        // ── ENQUÊTEURS — DECIDEUR ARM + ADMIN ─────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/enqueteurs",
                                "/api/users/enqueteurs/**"
                        ).hasAnyRole("ADMINISTRATEUR", "DECIDEUR_ARM")

                        .requestMatchers(HttpMethod.POST,
                                "/api/users/enqueteurs"
                        ).hasAnyRole("ADMINISTRATEUR", "DECIDEUR_ARM")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/users/enqueteurs/**"
                        ).hasAnyRole("ADMINISTRATEUR", "DECIDEUR_ARM")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/users/enqueteurs/**"
                        ).hasAnyRole("ADMINISTRATEUR", "DECIDEUR_ARM")

                        // ── ADMIN — GET ───────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/administrateurs",
                                "/api/users/administrateurs/**",
                                "/api/users/decideurs/**",
                                "/api/users/directeurs/dr/**",
                                "/api/users/directeurs/sddr/**",
                                "/api/users/chefs-cooperatifs",
                                "/api/users/agriculteurs",
                                "/api/users/agriculteurs/**"
                        ).hasRole("ADMINISTRATEUR")

                        // ── ADMIN — POST ──────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/users/administrateurs",
                                "/api/users/decideurs",
                                "/api/users/directeurs/**",
                                "/api/users/chefs-cooperatifs",
                                "/api/users/cooperatives"
                        ).hasRole("ADMINISTRATEUR")

                        // ── ADMIN + CHEF COOP — POST agriculteur ──────
                        .requestMatchers(HttpMethod.POST,
                                "/api/users/agriculteurs"
                        ).hasAnyRole("ADMINISTRATEUR", "CHEF_COOPERATIF")

                        // ── ADMIN — PUT ───────────────────────────────
                        .requestMatchers(HttpMethod.PUT,
                                "/api/users/administrateurs/**",
                                "/api/users/directeurs/dr/**",
                                "/api/users/directeurs/sddr/**"

                        ).hasRole("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/users/decideurs/**"
                        ).hasAnyRole("ADMINISTRATEUR" , "DECIDEUR_ARM")

                        // ── ADMIN + CHEF COOP — PUT coopératives ──────
                        .requestMatchers(HttpMethod.PUT,
                                "/api/users/cooperatives/**","/api/users/chefs-cooperatifs/**"
                        ).hasAnyRole("ADMINISTRATEUR", "CHEF_COOPERATIF")

                        // ── ADMIN — DELETE ────────────────────────────
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/users/cooperatives/**",
                                "/api/users/administrateurs/**",
                                "/api/users/directeurs/dr/**",
                                "/api/users/directeurs/sddr/**",
                                "/api/users/decideurs/**",
                                "/api/users/chefs-cooperatifs/**"
                        ).hasRole("ADMINISTRATEUR")

                        // ── ADMIN endpoints ───────────────────────────
                        .requestMatchers("/api/users/admin/**")
                        .hasRole("ADMINISTRATEUR")

                        // ── Tout le reste → authentifié ───────────────
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}