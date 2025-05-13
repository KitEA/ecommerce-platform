package com.kit.ecommerce_platform.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String SECRET_KEY = "testsecret123456789012345678901234567890";
        jwtService = new JwtService(SECRET_KEY);
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken("testuser");
        assertThat(token).isNotNull();
    }

    @Test
    void shouldValidateToken() {
        String token = jwtService.generateToken("testuser");
        boolean valid = jwtService.validateToken(token, "testuser");
        assertThat(valid).isTrue();
    }

    @Test
    void shouldFailValidationForWrongUsername() {
        String token = jwtService.generateToken("testuser");
        boolean valid = jwtService.validateToken(token, "otheruser");
        assertThat(valid).isFalse();
    }
}