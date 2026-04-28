package sn.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

                        // ── ROUTES PUBLIQUES ──────────────────────────
                        .requestMatchers("GET",
                                "/api/users/cooperatives",
                                "/api/users/cooperatives/**",
                                "/api/users/agriculteurs",
                                "/api/users/agriculteurs/**"
                        ).permitAll()

                        // Swagger
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // ── ADMINISTRATEUR ────────────────────────────
                        .requestMatchers("POST",
                                "/api/users/cooperatives",
                                "/api/users/administrateurs",
                                "/api/users/directeurs/**",
                                "/api/users/enqueteurs",
                                "/api/users/decideurs",
                                "/api/users/chefs-cooperatifs"
                        ).hasRole("ADMINISTRATEUR")

                        .requestMatchers("PUT",
                                "/api/users/cooperatives/**",
                                "/api/users/administrateurs/**"
                        ).hasRole("ADMINISTRATEUR")

                        .requestMatchers("DELETE",
                                "/api/users/cooperatives/**",
                                "/api/users/administrateurs/**"
                        ).hasRole("ADMINISTRATEUR")

                        .requestMatchers("GET",
                                "/api/users/administrateurs",
                                "/api/users/administrateurs/**"
                        ).hasRole("ADMINISTRATEUR")

                        // ── AGRICULTEUR ───────────────────────────────
                        .requestMatchers("GET",
                                "/api/users/agriculteurs/mon-profil"
                        ).hasRole("AGRICULTEUR")

                        // ── CHEF COOPERATIF ───────────────────────────
                        .requestMatchers("GET",
                                "/api/users/chefs-cooperatifs/mon-profil",
                                "/api/users/agriculteurs/mes-agriculteurs"
                        ).hasRole("CHEF_COOPERATIF")

                        // ── DIRECTEUR DR ──────────────────────────────
                        .requestMatchers("GET",
                                "/api/users/directeurs/dr/mon-profil"
                        ).hasRole("DIRECTEUR_DR")

                        // ── DIRECTEUR SDDR ────────────────────────────
                        .requestMatchers("GET",
                                "/api/users/directeurs/sddr/mon-profil"
                        ).hasRole("DIRECTEUR_SDDR")

                        // Tout le reste → authentifié
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}