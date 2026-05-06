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

                        // ── Actuator ──────────────────────────────────
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/error").permitAll()

                        // ── Parcelles ─────────────────────────────────
                        // Agriculteur → ses parcelles
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/parcelles/mes-parcelles"
                        ).hasRole("AGRICULTEUR")

                        // Chef coopératif → parcelles de sa coopérative
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/parcelles/ma-cooperative"
                        ).hasRole("CHEF_COOPERATIF")

                        // Directeur SDDR → parcelles de son département
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/parcelles/departement/**"
                        ).hasRole("DIRECTEUR_SDDR")

                        // Directeur DR → parcelles de sa région
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/parcelles/region/**"
                        ).hasRole("DIRECTEUR_DR")

                        // Création parcelle
                        .requestMatchers(HttpMethod.POST,
                                "/api/culture/parcelles"
                        ).hasAnyRole("AGRICULTEUR", "CHEF_COOPERATIF",
                                "DIRECTEUR_SDDR", "ADMINISTRATEUR")

                        // Modification parcelle
                        .requestMatchers(HttpMethod.PUT,
                                "/api/culture/parcelles/**"
                        ).hasAnyRole("AGRICULTEUR", "CHEF_COOPERATIF",
                                "DIRECTEUR_SDDR", "ADMINISTRATEUR")

                        // Suppression parcelle
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/culture/parcelles/**"
                        ).hasAnyRole("AGRICULTEUR", "DIRECTEUR_SDDR",
                                "ADMINISTRATEUR")

                        /*// Toutes les parcelles → Admin/Decideur
                        .requestMatchers(HttpMethod.GET,
                                "/api/culture/parcelles/toutes"
                        ).hasAnyRole("ADMINISTRATEUR", "DECIDEUR_ARM")
*/
                        // ── Tout le reste → authentifié ───────────────
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}