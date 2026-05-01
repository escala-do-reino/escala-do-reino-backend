package com.nathannolacio.escala_do_reino_backend.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(properties.getSecret())
        );
    }

    public String generateToken(String username, Long igrejaId) {
        return Jwts.builder()
                .claim("igrejaId", igrejaId)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractIgrejaId(String token) {
        Object igrejaId = extractAllClaims(token).get("igrejaId");
        if (igrejaId instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
