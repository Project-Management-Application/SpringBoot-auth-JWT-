package com.midou.tutorial.Projects.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserInfoResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}