package com.Vhytor.GoRent.services;

import com.Vhytor.GoRent.dtos.request.RegisterRequest;
import com.Vhytor.GoRent.dtos.response.LoginResponseDTO;
import com.Vhytor.GoRent.dtos.response.RegisterResponse;
import com.Vhytor.GoRent.enums.Role;
import com.Vhytor.GoRent.exceptions.EmailNotVerifiedException;
import com.Vhytor.GoRent.exceptions.InvalidCredentialsException;
import com.Vhytor.GoRent.exceptions.UserAlreadyExistsException;
import com.Vhytor.GoRent.exceptions.UserNotFoundException;
import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.repositories.UserRepository;
import com.Vhytor.GoRent.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    private static final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            RefreshTokenService refreshTokenService,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
    }

    /**
     * Registers a new TENANT.
     * Role is hardcoded here — the client has no influence over it.
     * Endpoint: POST /api/auth/register/tenant
     */
    public RegisterResponse registerTenant(RegisterRequest request) {
        return register(request, Role.TENANT);
    }

    /**
     * Registers a new LANDLORD.
     * Role is hardcoded here — the client has no influence over it.
     * Endpoint: POST /api/auth/register/landlord
     */
    public RegisterResponse registerLandlord(RegisterRequest request) {
        return register(request, Role.LANDLORD);
    }

    public RegisterResponse register(RegisterRequest registerRequest, Role role) {
        // Encode password before saving
        if (userRepository.findByUserEmail(registerRequest.getUserEmail()).isPresent()) {
            throw new UserAlreadyExistsException(registerRequest.getUserEmail());
        }

        String verificationCode = generateVerificationCode();

        // Build the user entity — role is set here by the server
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setUserEmail(registerRequest.getUserEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(role); // <-- server decides this not the client
        user.setVerified(false);
        user.setVerificationCode(verificationCode);

        User savedUser = userRepository.save(user);

        emailService.sendVerificationEmail(
                savedUser.getUserEmail(),
                savedUser.getFullName(),
                verificationCode
        );

        return new RegisterResponse(
                savedUser.getUserId(),
                savedUser.getFullName(),
                savedUser.getUserEmail(),
                savedUser.getRole().name()
        );

    }

    public void verifyEmail(String email, String code) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.isVerified()) {
            return; // Already verified — idempotent, no error needed
        }

        if (!code.equals(user.getVerificationCode())) {
            throw new InvalidCredentialsException();
        }

        user.setVerified(true);
        user.setVerificationCode(null); // Clear code — single use only
        userRepository.save(user);
    }

    public void resendVerificationCode(String email) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.isVerified()) {
            return; // Nothing to resend
        }

        String newCode = generateVerificationCode();
        user.setVerificationCode(newCode);
        userRepository.save(user);

        emailService.sendVerificationEmail(
                user.getUserEmail(),
                user.getFullName(),
                newCode
        );
    }

    /**
     * Authenticates a user and returns both access and refresh tokens.
     */
    @Transactional
    public LoginResponseDTO login(String userEmail, String password) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isVerified()) {
            throw new EmailNotVerifiedException(userEmail);
        }

        // Generate access token
        String accessToken = jwtProvider.generateAccessToken(user.getUserEmail());
        
        // Generate and persist refresh token
        String refreshToken = refreshTokenService.generateAndSaveRefreshToken(user, null);

        LoginResponseDTO response = new LoginResponseDTO(
                accessToken,
                user.getUserId(),
                user.getFullName(),
                user.getUserEmail(),
                user.getRole()
        );
        
        // Store refresh token in response (client should save it securely)
        // Note: In production, consider storing in httpOnly cookie instead
        response.setRefreshToken(refreshToken);
        
        return response;
    }

    private String generateVerificationCode() {
        int code = 100000 + secureRandom.nextInt(900000); // 6-digit
        return String.valueOf(code);
    }
}
