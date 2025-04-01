package com.midou.tutorial.Workspace.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private WorkspaceDTO workspace;
    private List<MemberDTO> members;
    private List<ProjectDTO> projects;
}