package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.TaskRequest;
import be.vives.taskmanager.application.dto.result.ProjectResult;
import be.vives.taskmanager.application.dto.result.TaskResult;
import be.vives.taskmanager.application.service.TaskService;
import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Task Management",
        description = "APIs for managing tasks within projects"
)
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks/{id}")
    @Operation(
            summary = "Get task by ID",
            description = "Retrieves a task if it belongs to a project owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found", content = @Content(schema = @Schema(implementation = TaskResult.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResult> getTaskById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.getTaskById(id, username));
    }

    @GetMapping("/projects/{projectId}/tasks")
    @Operation(
            summary = "Get tasks for a project",
            description = """
                    Retrieves tasks for a project owned by the user.
                    Optional filter by task status.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<TaskResult>> findAllTasksForProject(@PathVariable Long projectId, @RequestParam(required = false) TaskStatus status, Pageable pageable, Authentication authentication) {
        String username = authentication.getName();

        if (status != null) {
            return ResponseEntity.ok(taskService.findAllTasksByStatus(projectId, status, pageable, username));
        }

        return ResponseEntity.ok(taskService.findAllTasksForProject(projectId, pageable, username));
    }

    @PostMapping("/projects/{projectId}/tasks")
    @Operation(
            summary = "Create a task",
            description = "Creates a new task under a project owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(schema = @Schema(implementation = TaskResult.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
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
    @Operation(
            summary = "Update task",
            description = "Updates a task. Tasks with status DONE cannot be modified."
    )
    @ApiResponses(value = {
            @ApiResponse(

                    responseCode = "200",
                    description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskResult.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResult> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.updateTask(id, username, request));
    }

    @PatchMapping("/tasks/{id}")
    @Operation(
            summary = "Patch task",
            description = "Partially updates a task. Allows PATCH for completed/DONE tasks only if the only field being changed is status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskResult.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResult> patchTask(@PathVariable Long id, @RequestBody TaskRequest request, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.patchTask(id, username, request));
    }

    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete task",
            description = """
                    Deletes a task.
                    **ADMIN only**.
                    Completed tasks (DONE) cannot be deleted.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "400", description = "Task already completed"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
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