package com.midou.tutorial.Projects.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedTaskMemberDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
}
