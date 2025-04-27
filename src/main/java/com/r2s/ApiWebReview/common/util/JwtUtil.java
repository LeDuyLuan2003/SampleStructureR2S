package com.r2s.ApiWebReview.common.util;

import com.r2s.ApiWebReview.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key signingKey;
    private final long expirationTime;

    public long getExpirationTime() {
        return expirationTime;
    }

    public JwtUtil(@Value("${jwt.secret}") String secretKey,
                   @Value("${jwt.expiration}") long expirationTime) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationTime = expirationTime;
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail()) // sub
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("fullName", user.getFullname())
                .claim("roles", user.getRole().getName().name())
                .setIssuer("ApiWebReview Backend") // iss
                .setAudience("ApiWebReview Frontend") // aud
                .setIssuedAt(new Date()) // iat
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // exp
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public Long extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    public String extractFullName(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("fullName", String.class);
    }
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String issuer = claims.getIssuer();
            String audience = claims.getAudience();

            if (!"ApiWebReview Backend".equals(issuer)) {
                throw new JwtException("Invalid issuer");
            }

            if (!"ApiWebReview Frontend".equals(audience)) {
                throw new JwtException("Invalid audience");
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

}