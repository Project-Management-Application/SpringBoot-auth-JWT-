package com.midou.tutorial.student;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:3000") // Adjust if needed
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
