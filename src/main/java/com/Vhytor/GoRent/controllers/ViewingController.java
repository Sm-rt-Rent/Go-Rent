package com.Vhytor.GoRent.controllers;

import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.repositories.UserRepository;
import com.Vhytor.GoRent.services.ViewingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/viewings")
public class ViewingController {

    private final ViewingService viewingService;
    private final UserRepository userRepository;

    public ViewingController(ViewingService viewingService, UserRepository userRepository) {
        this.viewingService = viewingService;
        this.userRepository = userRepository;
    }

    /**
     * STEP 1 — Tenant initiates a viewing payment.
     * POST /api/viewings/{homeId}/initiate
     *
     * Returns the Paystack authorization_url.
     * The client must redirect the user to this URL to complete payment.
     */
    @PostMapping("/{homeId}/initiate")
    public ResponseEntity<Map<String, Object>> initiateViewing(
            @PathVariable Long homeId,
            Principal principal) {

        User user = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = viewingService.processViewingRequest(homeId, user);
        return ResponseEntity.ok(response);
    }

    /**
     * STEP 2 — Paystack calls this after payment is completed.
     * GET /api/payments/verify?reference=xxx
     *
     * Verifies the payment and returns the access code.
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> completeViewing(
            @RequestParam String reference) {

        Map<String, Object> response = viewingService.completeViewingAfterPayment(reference);
        return ResponseEntity.ok(response);
    }

    /**
     * Validates a viewing access code at the property.
     * POST /api/viewings/{homeId}/validate?code=123456
     */
    @PostMapping("/{homeId}/validate")
    public ResponseEntity<Map<String, Boolean>> validateCode(
            @PathVariable Long homeId,
            @RequestParam String code) {

        boolean valid = viewingService.validateAccessCode(homeId, code);
        return ResponseEntity.ok(Map.of("valid", valid));
    }
}