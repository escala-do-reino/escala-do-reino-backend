package com.nathannolacio.escala_do_reino_backend.auth.service;

import com.nathannolacio.escala_do_reino_backend.auth.security.JwtProperties;
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
        String token = jwtService.generateToken("test@test.com");

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3); // Header, Payload, Signature
    }

    @Test
    void extractUsername_Success() {
        String token = jwtService.generateToken("test@test.com");
        
        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo("test@test.com");
    }

    @Test
    void extractUsername_ExpiredToken_ThrowsException() {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(VALID_SECRET));
        String expiredToken = Jwts.builder()
                .setSubject("test@test.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // Expired 1 hour ago
                .signWith(key)
                .compact();

        assertThatThrownBy(() -> jwtService.extractUsername(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
