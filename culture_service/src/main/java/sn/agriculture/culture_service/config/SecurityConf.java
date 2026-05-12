package sn.agriculture.culture_service.config;

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
public class SecurityConf {

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

                        // ── PARCELLES ─────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/parcelles/mes-parcelles"
                        ).hasRole("AGRICULTEUR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/parcelles/ma-cooperative"
                        ).hasRole("CHEF_COOPERATIF")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/parcelles/mon-departement"
                        ).hasRole("DIRECTEUR_SDDR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/parcelles/ma-region"
                        ).hasRole("DIRECTEUR_DR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/parcelles/toutes"
                        ).hasAnyRole("ADMINISTRATEUR", "DECIDEUR_ARM")

                        .requestMatchers(HttpMethod.POST,
                                "/api/culture/parcelles"
                        ).hasAnyRole("AGRICULTEUR", "CHEF_COOPERATIF",
                                "DIRECTEUR_SDDR", "ADMINISTRATEUR")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/culture/parcelles/**"
                        ).hasAnyRole("AGRICULTEUR", "CHEF_COOPERATIF",
                                "DIRECTEUR_SDDR", "ADMINISTRATEUR")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/culture/parcelles/**"
                        ).hasAnyRole("AGRICULTEUR", "DIRECTEUR_SDDR",
                                "ADMINISTRATEUR")

                        // ── CULTURES ──────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/cultures/mes-cultures"
                        ).hasRole("AGRICULTEUR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/cultures/ma-cooperative"
                        ).hasRole("CHEF_COOPERATIF")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/cultures/mon-departement"
                        ).hasRole("DIRECTEUR_SDDR")
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/productions/mon-departement/surface-cultivee",
                                "/api/culture/productions/mon-departement/historique"
                        ).hasRole("DIRECTEUR_SDDR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/productions/ma-region/surface-cultivee",
                                "/api/culture/productions/ma-region/historique"
                        ).hasRole("DIRECTEUR_DR")
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/cultures/ma-region"
                        ).hasRole("DIRECTEUR_DR")

                        .requestMatchers(HttpMethod.POST,
                                "/api/culture/cultures"
                        ).hasAnyRole("AGRICULTEUR", "CHEF_COOPERATIF",
                                "DIRECTEUR_SDDR")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/culture/cultures/**"
                        ).hasAnyRole("AGRICULTEUR", "CHEF_COOPERATIF",
                                "DIRECTEUR_SDDR")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/culture/cultures/**"
                        ).hasAnyRole("AGRICULTEUR", "DIRECTEUR_SDDR",
                                "ADMINISTRATEUR")

                        // ── RÉCOLTES ──────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/recoltes/mes-recoltes"
                        ).hasRole("AGRICULTEUR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/recoltes/ma-cooperative"
                        ).hasRole("CHEF_COOPERATIF")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/recoltes/mon-departement"
                        ).hasRole("DIRECTEUR_SDDR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/recoltes/ma-region"
                        ).hasRole("DIRECTEUR_DR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/recoltes/toutes"
                        ).hasRole("DECIDEUR_ARM")

                        .requestMatchers(HttpMethod.POST,
                                "/api/culture/recoltes"
                        ).hasAnyRole("AGRICULTEUR", "CHEF_COOPERATIF",
                                "DIRECTEUR_SDDR")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/culture/recoltes/**"
                        ).hasRole("ADMINISTRATEUR")

                        // ── STATISTIQUES RÉCOLTES ─────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/recoltes/stats/**"
                        ).hasAnyRole("DECIDEUR_ARM", "DIRECTEUR_DR",
                                "DIRECTEUR_SDDR")

                        // ── PRODUCTIONS ───────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/productions/tableau-de-bord",
                                "/api/culture/productions/par-region",
                                "/api/culture/productions/par-annee",
                                "/api/culture/productions/alertes",
                                "/api/culture/productions/suivi-cultures"
                        ).hasRole("DECIDEUR_ARM")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/productions/ma-region/**"
                        ).hasRole("DIRECTEUR_DR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/productions/mon-departement/**"
                        ).hasRole("DIRECTEUR_SDDR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/productions/ma-cooperative/**"
                        ).hasRole("CHEF_COOPERATIF")
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/productions/departement/*/surface-cultivee",
                                "/api/culture/productions/region/*/surface-cultivee"
                        ).permitAll()
                        // ── Tout le reste → authentifié ───────────────
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}