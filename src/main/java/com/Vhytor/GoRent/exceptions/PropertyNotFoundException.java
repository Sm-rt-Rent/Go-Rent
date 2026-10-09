package com.Vhytor.GoRent.exceptions;

/**
 * Thrown when a requested property (Home) does not exist in the database.
 * Maps to HTTP 404 Not Found.
 */
public class PropertyNotFoundException extends GoRentException {

    public PropertyNotFoundException(Long homeId) {
        super("Property with ID " + homeId + " was not found.");
    }
}
