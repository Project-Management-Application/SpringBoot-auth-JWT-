package com.midou.tutorial.user.controllers;

import com.midou.tutorial.user.dto.OtpVerificationRequest;
import com.midou.tutorial.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/email-verification")
public class EmailController {
    private final UserRepository Repository;

    @PostMapping
    public ResponseEntity<String> VerifyOtp(@RequestBody OtpVerificationRequest Request) {
        var user = Repository.findByEmail(Request.getEmail()).orElseThrow(()->new RuntimeException("Email not found"));
        if(!user.getOtp().equals(Request.getOtp())) {
            return ResponseEntity.badRequest().body("OTP does not match");
        }
        user.setEnabled(true);
        user.setOtp(null);
        Repository.save(user);
        return ResponseEntity.ok("OTP verified");
    }
}
