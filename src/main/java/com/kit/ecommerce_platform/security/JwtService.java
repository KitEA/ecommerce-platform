package com.kit.ecommerce_platform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService() {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 3600000); // 1 hour

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey) // Use the SecretKey object directly
                .compact();
    }

    public boolean validateToken(String token, String username) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject().equals(username) && !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // Use the SecretKey object
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}