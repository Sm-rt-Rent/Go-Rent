package com.Vhytor.GoRent.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * RS256 (RSA SHA-256) JWT provider for asymmetric signing.
 * Uses a private key to sign tokens and a public key to verify.
 * Only loaded when app.jwt.algorithm=RS256
 * 
 * For development: Generate keys with:
 *   openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048
 *   openssl rsa -pubout -in private.pem -out public.pem
 * 
 * Then set environment variables:
 *   GORENT_JWT_PRIVATE="$(cat private.pem | base64 -w0)"
 *   GORENT_JWT_PUBLIC="$(cat public.pem | base64 -w0)"
 */
@Component
@ConditionalOnProperty(name = "app.jwt.algorithm", havingValue = "RS256", matchIfMissing = false)
public class JwtProviderRsa implements JwtProvider {
    
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    
    public JwtProviderRsa(
            @Value("${app.jwt.private-key:}") String privateKeyPem,
            @Value("${app.jwt.public-key:}") String publicKeyPem,
            @Value("${app.jwt.access-token-expiration-ms:900000}") long accessTokenExpirationMs,
            @Value("${app.jwt.refresh-token-expiration-ms:1209600000}") long refreshTokenExpirationMs
    ) throws Exception {
        this.privateKey = loadPrivateKey(privateKeyPem);
        this.publicKey = loadPublicKey(publicKeyPem);
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }
    
    @Override
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
    
    @Override
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(privateKey, SignatureAlgorithm.RS256)
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
        return getClaims(token).getSubject();
    }
    
    @Override
    public boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }
    
    @Override
    public long getTokenExpirationMs() {
        return accessTokenExpirationMs;
    }
    
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Load a private key from PEM-formatted string (base64-encoded).
     * Format: base64(-----BEGIN PRIVATE KEY-----...-----END PRIVATE KEY-----)
     */
    private PrivateKey loadPrivateKey(String keyPem) throws Exception {
        if (keyPem == null || keyPem.isBlank()) {
            throw new IllegalArgumentException("Private key PEM cannot be empty. Set app.jwt.private-key property.");
        }
        
        // Decode base64 if needed, or assume raw PEM
        String keyData = keyPem;
        if (!keyPem.startsWith("-----BEGIN")) {
            keyData = new String(Base64.getDecoder().decode(keyPem));
        }
        
        // Remove PEM headers and whitespace
        String privateKeyPem = keyData
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] decodedKey = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
    
    /**
     * Load a public key from PEM-formatted string (base64-encoded).
     * Format: base64(-----BEGIN PUBLIC KEY-----...-----END PUBLIC KEY-----)
     */
    private PublicKey loadPublicKey(String keyPem) throws Exception {
        if (keyPem == null || keyPem.isBlank()) {
            throw new IllegalArgumentException("Public key PEM cannot be empty. Set app.jwt.public-key property.");
        }
        
        // Decode base64 if needed, or assume raw PEM
        String keyData = keyPem;
        if (!keyPem.startsWith("-----BEGIN")) {
            keyData = new String(Base64.getDecoder().decode(keyPem));
        }
        
        // Remove PEM headers and whitespace
        String publicKeyPem = keyData
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyPem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}

