package com.Vhytor.GoRent.exceptions;

/**
 * Base exception for all SmartRent application errors.
 * All custom exceptions extend this class.
 */
public class GoRentException extends RuntimeException {

    public GoRentException(String message) {
        super(message);
    }

    public GoRentException(String message, Throwable cause) {
        super(message, cause);
    }
}
