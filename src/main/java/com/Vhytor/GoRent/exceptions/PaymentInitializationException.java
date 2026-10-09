package com.Vhytor.GoRent.exceptions;

/**
 * Thrown when the Paystack payment initialization request fails.
 * Maps to HTTP 502 Bad Gateway (upstream payment provider failed).
 */
public class PaymentInitializationException extends GoRentException {

    public PaymentInitializationException(String message) {
        super("Payment initialization failed: " + message);
    }

    public PaymentInitializationException(String message, Throwable cause) {
        super("Payment initialization failed: " + message, cause);
    }
}
