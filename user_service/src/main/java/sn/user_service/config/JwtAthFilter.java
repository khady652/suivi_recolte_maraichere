package sn.user_service.config;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

    @Component
    @RequiredArgsConstructor
    public class JwtAthFilter  extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain)
                throws ServletException, IOException {

            // Lire X-User-Id et X-User-Role depuis le header
            // Ces headers sont ajoutés par API Gateway
            String userIdStr = request.getHeader("X-User-Id");
            String userRole  = request.getHeader("X-User-Role");

            if (userIdStr != null && userRole != null) {
                try {
                    Integer userId = Integer.parseInt(userIdStr);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    List.of(new SimpleGrantedAuthority(
                                            "ROLE_" + userRole))
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                } catch (Exception e) {
                    logger.warn("Erreur lecture headers : "
                            + e.getMessage());
                }
            }

            filterChain.doFilter(request, response);
        }
    }

