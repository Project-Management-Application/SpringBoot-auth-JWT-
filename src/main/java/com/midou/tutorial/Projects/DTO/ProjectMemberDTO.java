package com.midou.tutorial.Projects.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProjectMemberDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
}