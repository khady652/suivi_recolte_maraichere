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
                                "/webjars/**"
                        ).permitAll()

                        // ── Actuator ──────────────────────────────────
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/error").permitAll()

                        // ── Endpoints internes AVANT hasRole ✅ ───────
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/geo/services-regionaux/*/affecter-directeur",
                                "/api/geo/services-departementaux/*/affecter-directeur"
                        ).permitAll()

                        // ✅ Lecture interne par les autres services
                        .requestMatchers(HttpMethod.GET,
                                "/api/geo/services-departementaux/*",
                                "/api/geo/services-regionaux/*"
                        ).permitAll()

                        // ── Lecture publique ──────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/geo/regions",
                                "/api/geo/regions/**",
                                "/api/geo/departements",
                                "/api/geo/departements/**"
                        ).permitAll()

                        // ── Admin POST ────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/geo/regions",
                                "/api/geo/departements",
                                "/api/geo/services-regionaux",
                                "/api/geo/services-departementaux"
                        ).hasRole("ADMINISTRATEUR")

                        // ── Admin PUT ─────────────────────────────────
                        .requestMatchers(HttpMethod.PUT,
                                "/api/geo/regions/**",
                                "/api/geo/departements/**",
                                "/api/geo/services-regionaux/**",
                                "/api/geo/services-departementaux/**"
                        ).hasRole("ADMINISTRATEUR")

                        // ── Admin DELETE ──────────────────────────────
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/geo/regions/**",
                                "/api/geo/departements/**",
                                "/api/geo/services-regionaux/**",
                                "/api/geo/services-departementaux/**"
                        ).hasRole("ADMINISTRATEUR")

                        // ── Admin GET liste complète ──────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/geo/services-regionaux",
                                "/api/geo/services-departementaux"
                        ).hasRole("ADMINISTRATEUR")

                        // ── Tout le reste → authentifié ───────────────
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}