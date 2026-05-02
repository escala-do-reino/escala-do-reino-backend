package com.nathannolacio.escala_do_reino_backend.core.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    // This is a 256-bit secret key encoded in Base64 (32 bytes)
    private final String VALID_SECRET = "c2VjcmV0S2V5Rm9ySldUV2l0aEF0TGVhc3QzMkJ5dGVzIQ==";

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecret()).thenReturn(VALID_SECRET);
    }

    @Test
    void generateToken_Success() {
        when(jwtProperties.getExpiration()).thenReturn(3600000L);
        String token = jwtService.generateToken(1L, "test@test.com", 10L);

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3); // Header, Payload, Signature
    }

    @Test
    void extractUserId_Success() {
        when(jwtProperties.getExpiration()).thenReturn(3600000L);
        String token = jwtService.generateToken(1L, "test@test.com", 10L);
        
        Long extractedUserId = jwtService.extractUserId(token);

        assertThat(extractedUserId).isEqualTo(1L);
    }

    @Test
    void extractEmail_Success() {
        when(jwtProperties.getExpiration()).thenReturn(3600000L);
        String token = jwtService.generateToken(1L, "test@test.com", 10L);
        
        String extractedEmail = jwtService.extractEmail(token);

        assertThat(extractedEmail).isEqualTo("test@test.com");
    }

    @Test
    void extractIgrejaId_Success() {
        when(jwtProperties.getExpiration()).thenReturn(3600000L);
        String token = jwtService.generateToken(1L, "test@test.com", 10L);

        Long extractedIgrejaId = jwtService.extractIgrejaId(token);

        assertThat(extractedIgrejaId).isEqualTo(10L);
    }

    @Test
    void extractUserId_ExpiredToken_ThrowsException() {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(VALID_SECRET));
        String expiredToken = Jwts.builder()
                .setSubject("1")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // Expired 1 hour ago
                .signWith(key)
                .compact();

        assertThatThrownBy(() -> jwtService.extractUserId(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
