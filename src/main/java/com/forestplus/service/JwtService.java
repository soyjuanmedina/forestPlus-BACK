package com.forestplus.service;

import com.forestplus.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final SecretKey refreshSecretKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.refresh-secret}") String refreshSecret,
            @Value("${jwt.expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.refreshSecretKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    /** ✅ Genera un Access Token */
    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** ✅ Genera un Refresh Token */
    public String generateRefreshToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(refreshSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** ✅ Valida un Access Token */
    public boolean validateAccessToken(String token) {
        return validateToken(token, secretKey);
    }

    /** ✅ Valida un Refresh Token */
    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshSecretKey);
    }

    /** ✅ Extrae email desde cualquier token */
    public String extractEmail(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /** ✅ Extrae claims (útil para refresh) */
    public Claims extractAllClaims(String token, boolean isRefreshToken) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        SecretKey key = isRefreshToken ? refreshSecretKey : secretKey;
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** 🔒 Método interno para validar tokens */
    private boolean validateToken(String token, SecretKey key) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
