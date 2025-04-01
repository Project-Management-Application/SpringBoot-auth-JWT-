package com.midou.tutorial.Workspace.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class MemberDTO {
    private Long id;
    private String fullName;
    private String email;
}