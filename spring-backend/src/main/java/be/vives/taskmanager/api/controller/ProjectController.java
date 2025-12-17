package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.ProjectRequest;
import be.vives.taskmanager.application.dto.result.ProjectResult;
import be.vives.taskmanager.application.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResult> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResult>> findAllProjects(Pageable pageable) {
        String username = "user";
        return ResponseEntity.ok(
                projectService.findAllProjectsForUser(username, pageable)
        );
    }

    @PostMapping
    public ResponseEntity<ProjectResult> createProject(@Valid @RequestBody ProjectRequest request) {
        String username = "user";
        ProjectResult result = projectService.createProject(username, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();

        return ResponseEntity.created(location).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResult> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResult> patchProject(@PathVariable Long id, @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.patchProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}