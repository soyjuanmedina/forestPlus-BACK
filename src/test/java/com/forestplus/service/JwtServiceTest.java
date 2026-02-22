package com.forestplus.service;

import com.forestplus.entity.UserEntity;
import com.forestplus.model.RolesEnum;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "MySuperSecretKeyForHS256MySuperSecretKeyForHS256";
    private final String refreshSecret = "MyRefreshSecretKeyForHS256MyRefreshSecretKeyForHS256";
    private final long accessExpiration = 1000 * 60 * 60; // 1 hora
    private final long refreshExpiration = 1000 * 60 * 60 * 24; // 24 horas

    private UserEntity user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, refreshSecret, accessExpiration, refreshExpiration);
        user = new UserEntity();
        user.setEmail("test@forestplus.com");
        user.setName("Test User");
        user.setRole(RolesEnum.USER);
    }

    @Test
    void testGenerateAndValidateAccessToken() {
        String token = jwtService.generateAccessToken(user);
        assertNotNull(token);
        assertTrue(jwtService.validateAccessToken(token));
        assertEquals("test@forestplus.com", jwtService.extractEmail(token));
        Claims claims = jwtService.extractAllClaims(token, false);
        assertEquals("Test User", claims.get("name"));
        assertEquals(RolesEnum.valueOf((String) claims.get("role")), RolesEnum.USER);
    }

    @Test
    void testGenerateAndValidateRefreshToken() {
        String token = jwtService.generateRefreshToken(user);
        assertNotNull(token);
        assertTrue(jwtService.validateRefreshToken(token));
        Claims claims = jwtService.extractAllClaims(token, true);
        assertEquals("test@forestplus.com", claims.getSubject());
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "abc.def.ghi";
        assertFalse(jwtService.validateAccessToken(invalidToken));
        assertFalse(jwtService.validateRefreshToken(invalidToken));
    }

    @Test
    void testBearerPrefix() {
        String token = "Bearer " + jwtService.generateAccessToken(user);
        assertEquals("test@forestplus.com", jwtService.extractEmail(token));
        assertTrue(jwtService.validateAccessToken(token));
    }
}