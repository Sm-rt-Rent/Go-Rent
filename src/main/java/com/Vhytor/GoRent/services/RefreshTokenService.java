package com.Vhytor.GoRent.services;

import com.Vhytor.GoRent.model.RefreshToken;
import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Service for managing refresh token lifecycle.
 * Handles generation, validation, storage, and revocation of refresh tokens.
 */
@Service
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_LENGTH = 32; // 256 bits

    
    // Token expiration: 14 days in milliseconds
    private static final long TOKEN_EXPIRATION_MS = 14L * 24 * 60 * 60 * 1000;
    
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    
    /**
     * Generate and persist a new refresh token for a user.
     * 
     * Returns the raw token (which the client should store securely).
     * The hashed version is stored in the database.
     * 
     * @param user The user to generate a token for
     * @param issuedFrom Optional IP/user agent for audit
     * @return Raw refresh token (send to client, never log or display)
     */
    @Transactional
    public String generateAndSaveRefreshToken(User user, String issuedFrom) {
        // Generate a random token
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        SECURE_RANDOM.nextBytes(tokenBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        
        // Hash the token for storage
        String tokenHash = hashToken(rawToken);
        
        // Create and persist the refresh token record
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setIssuedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(LocalDateTime.now().plusNanos(TOKEN_EXPIRATION_MS * 1_000_000));
        refreshToken.setRevoked(false);
        refreshToken.setIssuedFrom(issuedFrom);
        
        refreshTokenRepository.save(refreshToken);
        
        return rawToken;
    }
    
    /**
     * Validate a refresh token by comparing its hash in the database.
     * 
     * @param rawToken The raw refresh token from the client
     * @return The user associated with the token, or empty if invalid
     */
    @Transactional(readOnly = true)
    public Optional<User> validateRefreshToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }
        
        String tokenHash = hashToken(rawToken);
        
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenHash(tokenHash);
        if (tokenOpt.isEmpty()) {
            return Optional.empty();
        }
        
        RefreshToken token = tokenOpt.get();
        if (!token.isValid()) {
            return Optional.empty();
        }
        
        return Optional.of(token.getUser());
    }
    
    /**
     * Revoke a specific refresh token by its raw value.
     * After revocation, the token cannot be used again.
     * 
     * @param rawToken The raw refresh token to revoke
     */
    @Transactional
    public void revokeRefreshToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        
        String tokenHash = hashToken(rawToken);
        
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenHash(tokenHash);
        if (tokenOpt.isPresent()) {
            RefreshToken token = tokenOpt.get();
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        }
    }
    
    /**
     * Revoke all refresh tokens for a user (e.g., on logout).
     * 
     * @param user The user whose tokens should be revoked
     */
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }
    
    /**
     * Clean up expired refresh tokens from the database (optional maintenance task).
     * Call this periodically or in a scheduled job.
     */
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
    
    /**
     * Hash a refresh token using SHA-256.
     * We hash tokens to prevent disclosure if the database is compromised.
     * 
     * @param token The raw token to hash
     * @return Hashed token (hex-encoded)
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}

