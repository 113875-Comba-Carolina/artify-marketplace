package com.carolinacomba.marketplace.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private UserDetails userDetails;
    private final String SECRET = "mySecretKey123456789012345678901234567890";
    private final Long EXPIRATION = 86400000L; // 24 horas
    private final Long REFRESH_EXPIRATION = 604800000L; // 7 días

    @BeforeEach
    void setUp() {
        // Configurar valores usando ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", REFRESH_EXPIRATION);

        // Setup UserDetails
        userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .roles("CLIENTE")
                .build();
    }

    @Test
    void generateToken_DeberiaGenerarTokenValido() {
        // When
        String token = jwtUtil.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void generateRefreshToken_DeberiaGenerarRefreshTokenValido() {
        // When
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(jwtUtil.validateToken(refreshToken));
    }

    @Test
    void extractUsername_TokenValido_DeberiaExtraerUsername() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        String username = jwtUtil.extractUsername(token);

        // Then
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void extractExpiration_TokenValido_DeberiaExtraerFechaExpiracion() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_TokenValido_DeberiaRetornarTrue() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_TokenInvalido_DeberiaRetornarFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtUtil.validateToken(invalidToken, userDetails);
        });
    }

    @Test
    void validateToken_TokenSinUserDetails_DeberiaRetornarTrue() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_TokenInvalidoSinUserDetails_DeberiaRetornarFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void getTokenType_RefreshToken_DeberiaRetornarRefresh() {
        // Given
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // When
        String tokenType = jwtUtil.getTokenType(refreshToken);

        // Then
        assertEquals("refresh", tokenType);
    }

    @Test
    void getTokenType_AccessToken_DeberiaRetornarNull() {
        // Given
        String accessToken = jwtUtil.generateToken(userDetails);

        // When
        String tokenType = jwtUtil.getTokenType(accessToken);

        // Then
        assertNull(tokenType);
    }

    @Test
    void extractClaim_TokenValido_DeberiaExtraerClaim() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        String username = jwtUtil.extractClaim(token, claims -> claims.getSubject());

        // Then
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void isTokenExpired_TokenValido_DeberiaRetornarFalse() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        boolean isExpired = jwtUtil.extractExpiration(token).before(new Date());

        // Then
        assertFalse(isExpired);
    }
}