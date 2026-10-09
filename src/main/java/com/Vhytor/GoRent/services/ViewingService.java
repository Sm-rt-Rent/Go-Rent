package com.Vhytor.GoRent.services;

import com.Vhytor.GoRent.model.User;

import java.util.Map;

public interface ViewingService {
    // Contract: Give me a homeId and a User, I'll give you access details
    /**
     * STEP 1: Initializes a Paystack payment for viewing a property.
     * Returns the Paystack authorization_url to redirect the user to.
     * A PENDING ViewingRecord is saved at this point.
     */
    Map<String, Object> processViewingRequest(Long homeId, User user);
    /**
     * STEP 2: Called after Paystack redirects back to our callback URL.
     * Verifies the payment with Paystack, then generates and returns
     * the access code for the property.
     */
    Map<String, Object> completeViewingAfterPayment(String reference);

    /**
     * Validates an access code at the property door.
     * Marks the code as used so it cannot be reused.
     */
    boolean validateAccessCode(Long homeId, String code);
}
