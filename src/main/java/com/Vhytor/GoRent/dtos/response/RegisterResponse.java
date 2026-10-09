package com.Vhytor.GoRent.dtos.response;

/**
 * Response returned after a successful registration.
 * Never exposes the password or internal DB fields.
 */
public class RegisterResponse {

    private Long userId;
    private String fullName;
    private String userEmail;
    private String role;
    private String message;

    public RegisterResponse(Long userId, String fullName, String userEmail, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.userEmail = userEmail;
        this.role = role;
        this.message = "Registration successful. Welcome to SmartRent!";
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getUserEmail() { return userEmail; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
}
