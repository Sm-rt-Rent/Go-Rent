package com.Vhytor.GoRent.exceptions;

/**
 * Thrown when a user provides a wrong password during login.
 * Maps to HTTP 401 Unauthorized.
 */
public class InvalidCredentialsException extends GoRentException{
    public InvalidCredentialsException() {
        super("The email or password you entered is incorrect.");
    }
}
