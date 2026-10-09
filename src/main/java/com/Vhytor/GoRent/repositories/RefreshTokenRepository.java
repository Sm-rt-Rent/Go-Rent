package com.Vhytor.GoRent.repositories;

import com.Vhytor.GoRent.model.RefreshToken;
import com.Vhytor.GoRent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    /**
     * Find a refresh token by its hash.
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    
    /**
     * Find all non-revoked refresh tokens for a user.
     */
    List<RefreshToken> findByUserAndRevokedFalse(User user);
    
    /**
     * Find all refresh tokens for a user.
     */
    List<RefreshToken> findByUser(User user);
    
    /**
     * Check if a token exists and is valid (not revoked and not expired).
     */
    @Query("SELECT COUNT(rt) > 0 FROM RefreshToken rt " +
           "WHERE rt.tokenHash = :tokenHash AND rt.revoked = false AND rt.expiresAt > CURRENT_TIMESTAMP")
    boolean isTokenValid(String tokenHash);
    
    /**
     * Revoke all refresh tokens for a user (e.g., on logout).
     */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
    void revokeAllUserTokens(User user);
    
    /**
     * Delete expired tokens (cleanup job).
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}

