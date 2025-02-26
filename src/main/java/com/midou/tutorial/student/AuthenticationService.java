package com.midou.tutorial.student;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final EmailService emailService;
    private final StudentRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {

        String otp = generateOTP();
        var user = Student.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .otp(otp)
                .role(Role.USER)
                .build();
        repository.save(user);
        emailService.emailSenderOtp(user.getEmail(), user.getOtp(), user.getFullName());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
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
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .fullName(user.getFullName())
                .email(request.getEmail())
                .token(jwtToken)
                .build();
    }
}
