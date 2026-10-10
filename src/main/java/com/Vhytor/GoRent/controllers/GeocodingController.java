package com.Vhytor.GoRent.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/geocoding")
public class GeocodingController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/reverse")
    public ResponseEntity<Map> reverseGeocode(
            @RequestParam double lat,
            @RequestParam double lng) {

        String url = String.format(
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&zoom=18&addressdetails=1",
                lat, lng
        );

        // Nominatim requires a valid User-Agent identifying your application
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "GoRent-Property-Platform/1.0 (support@gorent.com)");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch address from coordinates"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAddress(@RequestParam String q) {
        String url = String.format(
                "https://nominatim.openstreetmap.org/search?format=json&q=%s&limit=1",
                java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "GoRent-Property-Platform/1.0 (support@gorent.com)");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
            List<Map> body = response.getBody();
            if (body != null && !body.isEmpty()) {
                return ResponseEntity.ok(body.get(0));
            }
            return ResponseEntity.status(404).body(Map.of("message", "Address not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to search address"));
        }
    }
}