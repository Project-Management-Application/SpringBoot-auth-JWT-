package com.midou.tutorial.user.dto;

import com.midou.tutorial.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private Role role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
}
