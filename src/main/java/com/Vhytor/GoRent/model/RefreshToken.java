package com.Vhytor.GoRent.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Persistent refresh token storage.
 * Stores hashed refresh tokens to prevent token theft if DB is compromised.
 * Tokens are linked to users and have an expiration time.
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_token_hash", columnList = "token_hash")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * SHA-256 hash of the actual refresh token.
     * Never store the raw token in the database.
     * This is compared against hash(received_token) during validation.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String tokenHash;
    
    /**
     * When this refresh token was issued.
     */
    @Column(nullable = false)
    private LocalDateTime issuedAt;
    
    /**
     * When this refresh token expires.
     * If current time > expiresAt, token is invalid.
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    /**
     * Whether this token has been revoked (e.g., user logged out).
     * Revoked tokens cannot be used to refresh even if not expired.
     */
    @Column(nullable = false)
    private boolean revoked = false;
    
    /**
     * IP address or user agent for audit purposes (optional).
     */
    private String issuedFrom;
    
    public boolean isValid() {
        return !revoked && LocalDateTime.now().isBefore(expiresAt);
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
    public void setIssuedFrom(String issuedFrom) {
        this.issuedFrom = issuedFrom;
    }

    public User getUser() {
        return user;
    }
    public String getTokenHash(){
        return tokenHash;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public boolean isRevoked() {
        return revoked;
    }
    public String getIssuedFrom() {
        return issuedFrom;
    }
}

