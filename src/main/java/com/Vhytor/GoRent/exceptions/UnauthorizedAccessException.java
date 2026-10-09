package com.Vhytor.GoRent.exceptions;

/**
 * Thrown when a user tries to access a resource they do not own or have permission for.
 * Maps to HTTP 403 Forbidden.
 */
public class UnauthorizedAccessException extends GoRentException {

    public UnauthorizedAccessException() {
        super("You do not have permission to access this resource.");
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
