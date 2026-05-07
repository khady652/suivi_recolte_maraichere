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

                        // ── Endpoints internes AVANT hasRole ✅ ───────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/agriculteurs/*/info",
                                "/api/users/agriculteurs/*/chef-info",
                                "/api/users/directeurs/sddr/*/info",
                                "/api/users/directeurs/dr/*/info"
                        ).permitAll()

                        // ── ROUTES PUBLIQUES ──────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/cooperatives",
                                "/api/users/cooperatives/**",
                                "/api/users/agriculteurs",
                                "/api/users/agriculteurs/**"
                        ).permitAll()

                        // ── ADMINISTRATEUR ────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/users/cooperatives",
                                "/api/users/administrateurs",
                                "/api/users/directeurs/**",
                                "/api/users/enqueteurs",
                                "/api/users/decideurs",
                                "/api/users/chefs-cooperatifs"
                        ).hasRole("ADMINISTRATEUR")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/users/cooperatives/**",
                                "/api/users/administrateurs/**"
                        ).hasRole("ADMINISTRATEUR")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/users/cooperatives/**",
                                "/api/users/administrateurs/**"
                        ).hasRole("ADMINISTRATEUR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/users/administrateurs",
                                "/api/users/administrateurs/**"
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

                        // ── ADMIN ─────────────────────────────────────
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