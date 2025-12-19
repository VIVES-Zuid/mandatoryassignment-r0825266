package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.TaskRequest;
import be.vives.taskmanager.application.dto.result.TaskResult;
import be.vives.taskmanager.application.service.TaskService;
import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
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
@CrossOrigin(exposedHeaders = "*")
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResult> getTaskById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.getTaskById(id, username));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Page<TaskResult>> findAllTasksForProject(@PathVariable Long projectId, @RequestParam(required = false) TaskStatus status, Pageable pageable, Authentication authentication) {
        String username = authentication.getName();

        if (status != null) {
            return ResponseEntity.ok(taskService.findAllTasksByStatus(projectId, status, pageable, username));
        }

        return ResponseEntity.ok(taskService.findAllTasksForProject(projectId, pageable, username));
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResult> createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request, Authentication authentication) {
        String username = authentication.getName();
        TaskResult result = taskService.createTask(projectId, username, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();

        return ResponseEntity.created(location).body(result);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResult> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.updateTask(id, username, request));
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<TaskResult> patchTask(@PathVariable Long id, @RequestBody TaskRequest request, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.patchTask(id, username, request));
    }

    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        taskService.deleteTask(id, username, isAdmin);
        return ResponseEntity.noContent().build();
    }

    /*@DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        taskService.deleteTask(id, username);
        return ResponseEntity.noContent().build();
    }*/
}