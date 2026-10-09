package com.Vhytor.GoRent.controllers;


import com.Vhytor.GoRent.dtos.request.RefreshTokenRequest;
import com.Vhytor.GoRent.dtos.request.RegisterRequest;
import com.Vhytor.GoRent.dtos.response.LoginResponseDTO;
import com.Vhytor.GoRent.dtos.response.RefreshTokenResponse;
import com.Vhytor.GoRent.dtos.response.RegisterResponse;
import com.Vhytor.GoRent.exceptions.InvalidCredentialsException;
import com.Vhytor.GoRent.exceptions.UserNotFoundException;
import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.repositories.UserRepository;
import com.Vhytor.GoRent.security.JwtProvider;
import com.Vhytor.GoRent.services.AuthService;
import com.Vhytor.GoRent.services.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public AuthController(
            AuthService authService,
            RefreshTokenService refreshTokenService,
            JwtProvider jwtProvider,
            UserRepository userRepository
    ) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    /**
     * POST /api/auth/register/tenant
     * Registers a new tenant. Role is assigned server-side as TENANT.
     */
    @PostMapping("/register/tenant")
    public ResponseEntity<RegisterResponse> registerTenant(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authService.registerTenant(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }

    /**
     * POST /api/auth/register/landlord
     * Registers a new landlord. Role is assigned server-side as LANDLORD.
     */
    @PostMapping("/register/landlord")
    public ResponseEntity<RegisterResponse> registerLandlord(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authService.registerLandlord(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody Map<String, String> credentials) {
        LoginResponseDTO loginResponseDTO = authService.login(
                credentials.get("userEmail"),
                credentials.get("password")
        );
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDTO);
    }

    /**
     * POST /api/auth/refresh
     * Uses a refresh token to obtain a new access token.
     * 
     * Request: { "refresh_token": "..." }
     * Response: { "access_token": "...", "expires_in": 900, "token_type": "Bearer" }
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        Optional<User> userOpt = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired refresh token"));
        }
        
        User user = userOpt.get();
        String newAccessToken = jwtProvider.generateAccessToken(user.getUserEmail());
        
        return ResponseEntity.ok(new RefreshTokenResponse(
                newAccessToken,
                900000  // 15 minutes in ms
        ));
    }

    /**
     * POST /api/auth/logout
     * Revokes the user's refresh tokens (log out from all devices).
     * Requires authentication (JWT in Authorization header).
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }
        
        String userEmail = authentication.getName();
        Optional<User> userOpt = userRepository.findByUserEmail(userEmail);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
        
        User user = userOpt.get();
        refreshTokenService.revokeAllUserTokens(user);
        
        return ResponseEntity.ok(Map.of("message", "Logged out successfully. All refresh tokens have been revoked."));
    }

    /**
     * POST /api/auth/verify
     * Verifies a user's email using the 6-digit code sent at registration.
     *
     * Request body: { "email": "...", "code": "123456" }
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @RequestBody Map<String, String> body) {
        authService.verifyEmail(body.get("email"), body.get("code"));
        return ResponseEntity.ok(Map.of("message", "Email verified successfully. You can now log in."));
    }

    /**
     * POST /api/auth/resend-code
     * Resends a fresh verification code to the user's email.
     *
     * Request body: { "email": "..." }
     */
    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, String>> resendCode(
            @RequestBody Map<String, String> body) {
        authService.resendVerificationCode(body.get("email"));
        return ResponseEntity.ok(Map.of("message", "A new verification code has been sent to your email."));
    }

}
