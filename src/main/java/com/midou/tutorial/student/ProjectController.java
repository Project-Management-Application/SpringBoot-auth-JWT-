package com.midou.tutorial.student;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final StudentService studentService;

    @PostMapping("/build/{userId}")
    public Project createProject(@PathVariable("userId") Long userId, @RequestBody Project projectRequest) {
        log.info("Received request to create project for user ID: {}", userId);

        // Fetch Student from DB
        Student student = studentService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + userId));


        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setStudent(student);


        return projectService.saveProject(project);
    }

    @GetMapping("/{userId}")
    public Project getProject(@PathVariable("userId") Long userId) {
        return projectService.getProjectByStudentId(userId)
                .orElseThrow(() -> new RuntimeException("No project found for student ID: " + userId));
    }
}
