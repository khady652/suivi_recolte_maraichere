package sn.agriculture.auth_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sn.agriculture.auth_service.security.JwtAuthFilter;

    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity
    @RequiredArgsConstructor
    public class SecurityConf {

        private final JwtAuthFilter jwtAuthFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    // Désactiver CSRF (inutile avec JWT)
                    .csrf(AbstractHttpConfigurer::disable)

                    // Pas de session HTTP (JWT = stateless)
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )

                    // Définir les autorisations par route
                    .authorizeHttpRequests(auth -> auth

                            // Routes publiques (sans token)
                            .requestMatchers("/api/auth/login").permitAll()
                            .requestMatchers("/api/auth/login/telephone").permitAll()
                            .requestMatchers("/api/auth/register").permitAll()
                            .requestMatchers("/api/auth/refresh").permitAll()
                            .requestMatchers("/api/auth/health").permitAll()

                            // Routes admin seulement
                            .requestMatchers("/api/auth/users/**")
                            .hasRole("ADMINISTRATEUR")

                            // Toutes les autres routes → token requis
                            .anyRequest().authenticated()
                    )

                    // Ajouter notre filtre JWT
                    .addFilterBefore(jwtAuthFilter,
                            UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        // BCrypt pour hasher les mots de passe
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }
    }

