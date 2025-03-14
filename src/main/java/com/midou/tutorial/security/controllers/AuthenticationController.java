package com.midou.tutorial.security.controllers;

import com.midou.tutorial.security.dto.*;
import com.midou.tutorial.security.exceptions.EmailNotVerifiedException;
import com.midou.tutorial.security.services.AuthenticationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;


@CrossOrigin(origins = "http://localhost:5173") // Adjust if needed
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) throws EmailNotVerifiedException {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/google-authenticate")
    public ResponseEntity<AuthenticationResponse> googleAuthenticate(@RequestParam String idToken) throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(service.authenticateWithGoogle(idToken));
    }

    @PostMapping("/Forgotpassword")
    public ResponseEntity<ForgotPassResponse> forgotPassword(
            @RequestBody ForgotPassRequest request
    ){
        return ResponseEntity.ok(service.forgotPass(request));
    }

    @PatchMapping("/resetpassword")
    public void resetPassword(@RequestParam String token,@RequestParam long userId,@RequestBody ResetPasswordRequest request ) throws EmailNotVerifiedException {
        service.resetPassword(token,userId,request.getNewPassword());
    }



}
