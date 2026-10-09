package com.Vhytor.GoRent.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * HS256 (HMAC SHA-256) JWT provider for symmetric signing.
 * Used as a fallback in development when RSA keys are not available.
 * NEVER use in production - use RS256 with JwtProviderRsa instead.
 */
@Component
@ConditionalOnProperty(name = "app.jwt.algorithm", havingValue = "HS256", matchIfMissing = false)
public class JwtProviderHs256 implements JwtProvider {
    
    private final Key signingKey;
    private final long accessTokenExpirationMs;
    
    public JwtProviderHs256(
            @Value("${app.jwt.symmetric-secret:c21hcnRyZW50LXN1cGVyLXNlY3JldC1rZXktZm9yLWp3dC0yMDI0}") String secretKey,
            @Value("${app.jwt.access-token-expiration-ms:900000}") long accessTokenExpirationMs
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }
    
    @Override
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    @Override
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    @Override
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
    
    @Override
    public long getTokenExpirationMs() {
        return accessTokenExpirationMs;
    }
}

