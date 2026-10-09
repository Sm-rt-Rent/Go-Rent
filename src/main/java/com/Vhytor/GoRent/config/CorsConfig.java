package com.Vhytor.GoRent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration for SmartRent.
 *
 * In development: allows localhost:5173 (Vite dev server)
 * In production:  only allows your real frontend domain
 *                 set via CORS_ALLOWED_ORIGINS environment variable
 *
 * Example production value:
 *   CORS_ALLOWED_ORIGINS= https://gorent.com,https://www.smartrent.com
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Parse comma-separated origins from environment variable
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(origins);

        // Only allow the HTTP methods our API actually uses
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow Authorization header for JWT and Content-Type for JSON
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // Allow frontend to read Authorization header in responses
        config.setExposedHeaders(List.of("Authorization"));

        // Allow cookies and Authorization headers to be sent cross-origin
        config.setAllowCredentials(true);

        // Cache preflight response for 1 hour — reduces OPTIONS requests
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
