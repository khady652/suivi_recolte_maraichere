package sn.agriculture.geo_service.config;

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
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/api/geo/regions",
                                "/api/geo/regions/**",
                                "/api/geo/departements",
                                "/api/geo/departements/**",
                                "/api/geo/services-regionaux/*/affecter-directeur",
                                "/api/geo/services-departementaux/*/affecter-directeur"
                        ).permitAll()

                        // ── Actuator + Error ──────────────────────────
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/error").permitAll()

                        // ── Endpoints internes (autres microservices) ─
                        .requestMatchers(HttpMethod.GET,
                                "/api/geo/services-departementaux/*",
                                "/api/geo/services-regionaux/*",
                                "/api/geo/departements/*",
                                "/api/geo/regions/*"
                        ).permitAll()

                        // ── Endpoints affectation ─────────────────────
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/geo/services-regionaux/*/affecter-directeur",
                                "/api/geo/services-departementaux/*/affecter-directeur"
                        ).permitAll()

                        // ── DIRECTEUR SDDR → son département ──────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/geo/departements/mon-departement"
                        ).hasRole("DIRECTEUR_SDDR")

                        // ── DIRECTEUR DR → sa région ──────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/geo/regions/ma-region"
                        ).hasRole("DIRECTEUR_DR")

                        // ── ADMINISTRATEUR → CRUD ─────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/geo/regions",
                                "/api/geo/departements",
                                "/api/geo/services-regionaux",
                                "/api/geo/services-departementaux"
                        ).hasRole("ADMINISTRATEUR")

                        .requestMatchers(HttpMethod.POST,
                                "/api/geo/regions",
                                "/api/geo/departements",
                                "/api/geo/services-regionaux",
                                "/api/geo/services-departementaux"
                        ).hasRole("ADMINISTRATEUR")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/geo/regions/**",
                                "/api/geo/departements/**",
                                "/api/geo/services-regionaux/**",
                                "/api/geo/services-departementaux/**"
                        ).hasRole("ADMINISTRATEUR")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/geo/regions/**",
                                "/api/geo/departements/**",
                                "/api/geo/services-regionaux/**",
                                "/api/geo/services-departementaux/**"
                        ).hasRole("ADMINISTRATEUR")

                        // ── Tout le reste → authentifié ───────────────
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}