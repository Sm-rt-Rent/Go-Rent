package com.Vhytor.GoRent.exceptions;

/**
 * Thrown when a user cannot be found by their email or ID.
 * Maps to HTTP 404 Not Found.
 */

public class UserNotFoundException extends GoRentException{

    public UserNotFoundException(String email) {
            super("No account found with email: " + email);
        }

        public UserNotFoundException(Long userId) {
            super("No account found with ID: " + userId);
        }
}

