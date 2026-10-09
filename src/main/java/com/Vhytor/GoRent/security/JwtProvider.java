package com.Vhytor.GoRent.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Abstraction for JWT token generation, validation, and claims extraction.
 * Implementations can use different signing algorithms (HS256, RS256, etc).
 */
public interface JwtProvider {

    /**
     * Generate an access token for a user.
     * Short-lived token (e.g., 15 minutes).
     */
    String generateAccessToken(String username);

    /**
     * Generate a refresh token for a user.
     * Long-lived token (e.g., 14 days).
     * Refresh tokens are typically stored hashed in the database.
     */
    String generateRefreshToken(String username);

    /**
     * Validate a JWT token signature, expiration, and claims.
     */
    boolean validateToken(String token, UserDetails userDetails);

    /**
     * Extract the username (subject) from a token.
     */
    String extractUsername(String token);

    /**
     * Check if token is expired.
     */
    boolean isTokenExpired(String token);

    /**
     * Get the token expiration time in milliseconds from now.
     * Used to determine refresh token lifetime in database.
     */
    long getTokenExpirationMs();
}

