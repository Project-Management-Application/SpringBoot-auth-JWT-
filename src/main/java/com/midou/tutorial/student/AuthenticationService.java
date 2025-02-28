package com.midou.tutorial.student;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
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
    private final PasswordResetTokenService passwordResetTokenService;

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

    public ForgotPassResponse forgotPass(ForgotPassRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var forgotPassToken = passwordResetTokenService.generatePasswordResetToken(user.getId());
        String link = "http://localhost:3000/ResetPassword?token=" + forgotPassToken + "&userId=" + user.getId();
        emailService.emailSenderForgetPassword(user.getEmail(), user.getFullName(), link);

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
