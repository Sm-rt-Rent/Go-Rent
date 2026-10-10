package com.Vhytor.GoRent.exceptions;

/**
 * Thrown when a landlord tries to delete a property that already has
 * paid viewings. Those records are tied to real payments and possibly
 * still-valid access codes, so the listing must be kept.
 * Maps to HTTP 409 Conflict.
 */
public class PropertyHasViewingException extends GoRentException {

  public PropertyHasViewingException(Long homeId) {
    super("Property " + homeId + " has paid viewings and cannot be deleted.");
  }
}