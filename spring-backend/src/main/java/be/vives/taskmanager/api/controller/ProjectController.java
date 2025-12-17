package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.ProjectRequest;
import be.vives.taskmanager.application.dto.result.ProjectResult;
import be.vives.taskmanager.application.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/projects")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResult> getProjectById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(projectService.getProjectById(id, username));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResult>> findAllProjects(Pageable pageable, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(projectService.findAllProjectsForUser(username, pageable));
    }

    @PostMapping
    public ResponseEntity<ProjectResult> createProject(@Valid @RequestBody ProjectRequest request, Authentication authentication) {
        String username = authentication.getName();
        ProjectResult result = projectService.createProject(username, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();

        return ResponseEntity.created(location).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResult> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(projectService.updateProject(id, username, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResult> patchProject(@PathVariable Long id, @RequestBody ProjectRequest request, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(projectService.patchProject(id, username, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        projectService.deleteProject(id, username, isAdmin);
        return ResponseEntity.noContent().build();
    }

    /*@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        projectService.deleteProject(id, username);
        return ResponseEntity.noContent().build();
    }*/
}