package com.Vhytor.GoRent.dtos.response;

import com.Vhytor.GoRent.enums.Role;

/**
 * Response returned after a successful login.
 * Returns the JWT access token and refresh token alongside user details.
 */
public class LoginResponseDTO {

    private String token;
    private String refreshToken;
    private Long userId;
    private String fullName;
    private String userEmail;
    private Role role;

    public LoginResponseDTO(String token, Long userId, String fullName, String userEmail, Role role) {
        this.token = token;
        this.userId = userId;
        this.fullName = fullName;
        this.userEmail = userEmail;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getUserEmail() { return userEmail; }
    public Role getRole() { return role; }
}
