package com.Vhytor.GoRent.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response body for token refresh endpoint.
 * Returns a new access token (and optionally a new refresh token).
 */
public class RefreshTokenResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("token_type")
    private String tokenType = "Bearer";
    
    @JsonProperty("expires_in")
    private long expiresIn; // seconds
    
    @JsonProperty("refresh_token")
    private String refreshToken; // Optional: rotated refresh token
    
    public RefreshTokenResponse() {}
    
    public RefreshTokenResponse(String accessToken, long expiresInMs) {
        this.accessToken = accessToken;
        this.expiresIn = expiresInMs / 1000; // Convert to seconds
    }
    
    public RefreshTokenResponse(String accessToken, long expiresInMs, String newRefreshToken) {
        this.accessToken = accessToken;
        this.expiresIn = expiresInMs / 1000;
        this.refreshToken = newRefreshToken;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

