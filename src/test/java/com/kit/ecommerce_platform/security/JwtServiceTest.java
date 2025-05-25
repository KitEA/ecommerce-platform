package com.kit.ecommerce_platform.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
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