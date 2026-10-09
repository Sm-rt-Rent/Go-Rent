package com.Vhytor.GoRent.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for token refresh endpoint.
 * Client sends their current refresh token to get a new access token.
 */
public class RefreshTokenRequest {
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    public RefreshTokenRequest() {}
    
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

