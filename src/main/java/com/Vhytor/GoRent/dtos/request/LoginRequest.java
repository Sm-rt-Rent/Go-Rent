package com.Vhytor.GoRent.dtos.request;

public class LoginRequest {

    private String userEmail;
    private String password;

    // Default Constructor
    public LoginRequest() {
    }

    // Parameterized Constructor
    public LoginRequest(String userEmail, String password) {
        this.userEmail = userEmail;
        this.password = password;
    }

    // Manual Getters and Setters
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
