package com.forestplus.config;

import com.forestplus.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    	
        String path = request.getRequestURI();
        System.out.println("📡 path: " + path);
        System.out.println("📡 Petición recibida: " + request.getMethod() + " " + request.getRequestURI());
        System.out.println("    Origin: " + request.getHeader("Origin"));
        System.out.println("    Authorization: " + request.getHeader("Authorization"));

        // Saltar Swagger y OpenAPI
        if (path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-ui") ||
            path.equals("/swagger-ui.html") ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Opcional: validación extra contra jwtExpirationMs
            if (claims.getExpiration() != null && claims.getExpiration().getTime() < System.currentTimeMillis()) {
                throw new ExpiredJwtException(null, claims, "Token expirado");
            }

            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                String authority = "ROLE_" + role;
                System.out.println("📌 Usuario autenticado: " + email);
                System.out.println("📌 Rol desde JWT: " + role);
                System.out.println("📌 GrantedAuthority que se asignará: " + authority);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Debug adicional para ver qué tiene Spring Security
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .forEach(a -> System.out.println("📌 Authority en SecurityContext: " + a.getAuthority()));
            }

        } catch (ExpiredJwtException e) {
            System.out.println("JWT expirado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("JWT inválido: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/api/webhooks/loops")
            || path.startsWith("/development/api/webhooks/loops");
    }
}
