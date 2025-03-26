package com.midou.tutorial.security.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.midou.tutorial.security.enums.Subscription;
import org.springframework.beans.factory.annotation.Value;
import com.midou.tutorial.security.dto.*;
import com.midou.tutorial.security.entities.User;
import com.midou.tutorial.security.enums.Role;
import com.midou.tutorial.security.exceptions.EmailNotVerifiedException;
import com.midou.tutorial.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final EmailService emailService;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenService passwordResetTokenService;

    @Value("${google.client.id}")
    private String googleClientId;


    public AuthenticationResponse register(RegisterRequest request) {

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        String otp = generateOTP();
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .otp(otp)
                .role(request.getRole())
                .subscription(Subscription.valueOf("STARTER"))
                .build();
        repository.save(user);
        emailService.emailSenderOtp(user.getEmail(), user.getOtp(), user.getFirstName(),user.getLastName());
        var jwtToken = jwtService.generateToken(user, true, user.getRole().name());
        return AuthenticationResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .token(jwtToken)
                .build();
    }

    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // Ensures a 6-digit number
        return String.valueOf(otp);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws EmailNotVerifiedException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean enabled = user.getEnabled();
        if (!enabled) {
            throw new EmailNotVerifiedException("email not verified");
        }
        var jwtToken = jwtService.generateToken(user, true, user.getRole().name());
        return AuthenticationResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(request.getEmail())
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticateWithGoogle(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new AuthenticationServiceException("Invalid Google ID Token");
        }

        // Debug log
        System.out.println("Google ID Token successfully verified");

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String googleId = payload.getSubject();

        System.out.println("User email from Google: " + email);
        System.out.println("User full name from Google: " + firstName + " " + lastName);

        Optional<User> existingUser = repository.findByEmail(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            System.out.println("Existing user found: " + user.getEmail());
        } else {
            user = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .googleId(googleId)
                    .role(Role.USER)
                    .enabled(true)
                    .isGoogleUser(true)
                    .build();
            repository.save(user);
            System.out.println("New user created: " + user.getEmail());
        }

        var jwtToken = jwtService.generateToken(user, true, user.getRole().name());
        System.out.println("JWT Token Generated: " + jwtToken);

        return AuthenticationResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .token(jwtToken)
                .build();
    }

    public ForgotPassResponse forgotPass(ForgotPassRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var forgotPassToken = passwordResetTokenService.generatePasswordResetToken(user.getId());
        String link = "http://localhost:5173/change-password?token=" + forgotPassToken + "&userId=" + user.getId();
        emailService.emailSenderForgetPassword(user.getEmail(), user.getFirstName(), user.getLastName(), link);

        return ForgotPassResponse.builder()
                .userId(user.getId())
                .token(forgotPassToken)
                .build();
    }

    public void resetPassword(String token, Long userId,String newPassword) throws EmailNotVerifiedException {
        if (!(passwordResetTokenService.isPasswordResetTokenValid(token, userId))) {
            throw new AuthenticationServiceException("Invalid token");
        }
        var user = repository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.isEnabled()) {
            throw new EmailNotVerifiedException("User account is not active.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);

        System.out.println("Password reset for user: " +  user.getEmail());
    }
}
