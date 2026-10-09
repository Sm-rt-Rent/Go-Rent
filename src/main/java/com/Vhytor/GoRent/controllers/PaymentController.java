package com.Vhytor.GoRent.controllers;

import com.Vhytor.GoRent.repositories.UserRepository;
import com.Vhytor.GoRent.services.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public PaymentController(PaymentService paymentService, UserRepository userRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

//    @PostMapping("/initialize/{homeId}")
//    public ResponseEntity<Map<String, String>> initialize(@PathVariable Long homeId, @RequestParam BigDecimal amount) {
//        // 1. Identify the Tenant
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User user = userRepository.findByUserEmail(auth.getName())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 2. Call the Service to get the Paystack URL
//        Map<String,String> paymentUrl = paymentService.initializeTransaction(user, amount, homeId);
//
//        // 3. Return the URL so the frontend can redirect the user
//        return ResponseEntity.ok(Map.of("checkoutUrl", paymentUrl));
//    }
}
