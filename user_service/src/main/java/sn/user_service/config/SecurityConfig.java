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

                        // ── ADMIN — GET ───────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/administrateurs",
                                "/api/users/administrateurs/**",
                                "/api/users/enqueteurs",
                                "/api/users/enqueteurs/**",
                                "/api/users/decideurs/**",
                                "/api/users/directeurs/dr/**",
                                "/api/users/directeurs/sddr/**",
                                "/api/users/chefs-cooperatifs",
                                "/api/users/chefs-cooperatifs/**",
                                "/api/users/agriculteurs",
                                "/api/users/agriculteurs/**"
                        ).hasRole("ADMINISTRATEUR")

                        // ── ADMIN — POST ──────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/users/administrateurs",
                                "/api/users/decideurs",
                                "/api/users/directeurs/**",
                                "/api/users/chefs-cooperatifs"
                        ).hasRole("ADMINISTRATEUR")

                        // ── ADMIN + CHEF COOP — POST agriculteur ──────
                        .requestMatchers(HttpMethod.POST,
                                "/api/users/agriculteurs"
                        ).hasAnyRole("ADMINISTRATEUR", "CHEF_COOPERATIF")

                        // ── ADMIN + DIRECTEUR SDDR — POST enquêteur ───
                        .requestMatchers(HttpMethod.POST,
                                "/api/users/enqueteurs"
                        ).hasAnyRole("ADMINISTRATEUR", "DIRECTEUR_SDDR")

                        // ── ADMIN — PUT ───────────────────────────────
                        .requestMatchers(HttpMethod.PUT,
                                "/api/users/administrateurs/**",
                                "/api/users/decideurs/**",
                                "/api/users/directeurs/dr/**",
                                "/api/users/directeurs/sddr/**",
                                "/api/users/enqueteurs/**",
                                "/api/users/chefs-cooperatifs/**"
                        ).hasRole("ADMINISTRATEUR")

                        // ── ADMIN + AGRICULTEUR — PUT profil ──────────
                        .requestMatchers(HttpMethod.PUT,
                                "/api/users/agriculteurs/**"
                        ).hasAnyRole("ADMINISTRATEUR", "AGRICULTEUR")

                        // ── ADMIN + CHEF COOP — PUT coopératives ──────
                        .requestMatchers(HttpMethod.PUT,
                                "/api/users/cooperatives/**"
                        ).hasAnyRole("ADMINISTRATEUR", "CHEF_COOPERATIF")

                        // ── ADMIN — DELETE ────────────────────────────
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/users/cooperatives/**",
                                "/api/users/administrateurs/**",
                                "/api/users/directeurs/dr/**",
                                "/api/users/directeurs/sddr/**",
                                "/api/users/enqueteurs/**",
                                "/api/users/decideurs/**",
                                "/api/users/chefs-cooperatifs/**",
                                "/api/users/agriculteurs/**"
                        ).hasRole("ADMINISTRATEUR")

                        // ── AGRICULTEUR ───────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/agriculteurs/mon-profil"
                        ).hasRole("AGRICULTEUR")

                        // ── CHEF COOPERATIF ───────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/chefs-cooperatifs/mon-profil",
                                "/api/users/agriculteurs/mes-agriculteurs"
                        ).hasRole("CHEF_COOPERATIF")

                        // ── DIRECTEUR DR ──────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/directeurs/dr/mon-profil"
                        ).hasRole("DIRECTEUR_DR")

                        // ── DIRECTEUR SDDR ────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/directeurs/sddr/mon-profil"
                        ).hasRole("DIRECTEUR_SDDR")

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