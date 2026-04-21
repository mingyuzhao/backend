package com.toyota.tmmc.security;

import com.toyota.tmmc.config.JwtProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(new JwtProperties(
            "12345678901234567890123456789012",
            60000
    ));

    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("alice", "USER");

        assertTrue(jwtUtil.validateToken(token));
        assertTrue(jwtUtil.validateToken(token, "alice"));
        assertFalse(jwtUtil.validateToken(token, "bob"));
        assertEquals("alice", jwtUtil.extractUsername(token));
        assertEquals("USER", jwtUtil.extractRole(token));
    }

    @Test
    void shouldRejectMalformedToken() {
        assertFalse(jwtUtil.validateToken("not-a-jwt"));
        assertFalse(jwtUtil.validateToken("not-a-jwt", "alice"));
    }
}


