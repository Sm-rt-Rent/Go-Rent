package com.Vhytor.GoRent.controllers;

import com.Vhytor.GoRent.services.ViewingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/lock")
public class SmartLockController {

    private final ViewingService viewingService;

    public SmartLockController(ViewingService viewingService) {
        this.viewingService = viewingService;
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestParam Long homeId, @RequestParam String code) {
        boolean isValid = viewingService.validateAccessCode(homeId, code);

        Map<String, Object> response = new HashMap<>();
        if (isValid) {
            response.put("status", "OPEN");
            response.put("message", "Access granted. Welcome home!");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "LOCKED");
            response.put("message", "Invalid or expired code.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }
}
