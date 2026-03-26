package com.usermanagement.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        org.springframework.test.util.ReflectionTestUtils.setField(
                jwtTokenProvider, "jwtSecret", "mySecretKeyForJwtTokenSigningMustBeAtLeast256BitsLong");
        org.springframework.test.util.ReflectionTestUtils.setField(
                jwtTokenProvider, "accessTokenExpiration", 1800000L); // 30 minutes
        org.springframework.test.util.ReflectionTestUtils.setField(
                jwtTokenProvider, "refreshTokenExpiration", 604800000L); // 7 days
    }

    @Test
    void generateAccessToken_Success() {
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "testuser",
                        "password",
                        Collections.singletonList(new SimpleGrantedAuthority("users:read"))
                );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateAccessToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void generateRefreshToken_Success() {
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "testuser",
                        "password",
                        Collections.emptyList()
                );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateRefreshToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertTrue(jwtTokenProvider.isRefreshToken(token));
    }

    @Test
    void getUsernameFromToken_Success() {
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "testuser",
                        "password",
                        Collections.emptyList()
                );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateAccessToken(authentication);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void isRefreshToken_AccessToken_ReturnsFalse() {
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "testuser",
                        "password",
                        Collections.emptyList()
                );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateAccessToken(authentication);

        assertFalse(jwtTokenProvider.isRefreshToken(token));
    }

    @Test
    void generateAccessTokenFromUsername_Success() {
        String token = jwtTokenProvider.generateAccessTokenFromUsername("testuser");

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    void validateToken_NullToken_ReturnsFalse() {
        assertFalse(jwtTokenProvider.validateToken(null));
    }

    @Test
    void validateToken_EmptyToken_ReturnsFalse() {
        assertFalse(jwtTokenProvider.validateToken(""));
    }
}
