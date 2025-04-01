package com.midou.tutorial.Workspace.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class MemberDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}