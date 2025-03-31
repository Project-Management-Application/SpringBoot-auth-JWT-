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
    private String fullName;
    private String email;
    private String password;
}
