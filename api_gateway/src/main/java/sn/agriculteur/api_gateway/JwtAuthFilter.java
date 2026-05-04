package sn.agriculteur.api_gateway;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

    @Component
    @Slf4j
    public class JwtAuthFilter  implements GlobalFilter, Ordered {

        @Value("${jwt.secret}")
        private String jwtSecret;

        private final List<String> publicRoutes = List.of(
                "/api/auth/login",
                "/api/auth/login/telephone",
                "/api/auth/register",
                "/api/auth/refresh",
                "/api/auth/forgot-password",
                "/api/auth/reset-password",
                "/api/auth/health",
                "/api/users/agriculteurs",
                "/api/users/cooperatives",
                "/actuator/health",
                "/api/auth/internal",

                // Swagger
                "/swagger-ui.html",
                "/swagger-ui",
                "/v3/api-docs",
                "/auth-service/v3/api-docs",
                "/users-service/v3/api-docs",
                "/geo_service/v3/api-docs",
                "/webjars"
        );

        @Override
        public Mono<Void> filter(
                ServerWebExchange exchange,
                GatewayFilterChain chain) {

            String path = exchange.getRequest()
                    .getPath().toString();

            if (isPublicRoute(path)) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (authHeader == null ||
                    !authHeader.startsWith("Bearer ")) {
                log.warn("Token manquant pour : {}", path);
                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = extractAllClaims(token);
                Integer userId = claims.get("userId", Integer.class);
                String role = claims.get("role", String.class);

                log.debug("Utilisateur {} ({}) accède à {}",
                        userId, role, path);

                ServerWebExchange modifiedExchange = exchange
                        .mutate()
                        .request(r -> r
                                .header("X-User-Id", userId.toString())
                                .header("X-User-Role", role)
                        )
                        .build();

                return chain.filter(modifiedExchange);

            } catch (ExpiredJwtException e) {
                log.warn("Token expiré : {}", path);
                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();

            } catch (JwtException e) {
                log.warn("Token invalide : {}", path);
                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        @Override
        public int getOrder() {
            return -1;
        }

        private boolean isPublicRoute(String path) {
            return publicRoutes.stream()
                    .anyMatch(route -> path.equals(route) ||
                            path.startsWith(route + "/") &&
                                    !path.contains("mon-profil") &&
                                    !path.contains("mes-agriculteurs"));
        }

        private Claims extractAllClaims(String token) {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }

        private SecretKey getSigningKey() {
            byte[] keyBytes = Decoders.BASE64.decode(
                    java.util.Base64.getEncoder()
                            .encodeToString(jwtSecret.getBytes())
            );
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }

