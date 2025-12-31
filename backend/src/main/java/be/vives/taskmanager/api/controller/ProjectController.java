package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.ProjectRequest;
import be.vives.taskmanager.application.dto.result.ProjectResult;
import be.vives.taskmanager.application.service.ProjectService;
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
//@CrossOrigin(exposedHeaders = "*")
@RequestMapping("api/projects")
@Tag(
        name = "Project Management",
        description = "APIs for managing projects owned by the authenticated user"
)
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get project by ID",
            description = "Retrieves a project if it is owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project found",
                    content = @Content(schema = @Schema(implementation = ProjectResult.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Access denied - not owner"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ProjectResult> getProjectById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(projectService.getProjectById(id, username));
    }

    @GetMapping
    @Operation(
            summary = "Get all projects",
            description = """
                    Retrieves a paginated list of projects owned by the authenticated user.

                    **Pagination parameters**:
                    - `page`: Page number (0-indexed)
                    - `size`: Items per page
                    - `sort`: Sorting (e.g. `name,asc`)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved projects",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid")
    })
    public ResponseEntity<Page<ProjectResult>> findAllProjects(Pageable pageable, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(projectService.findAllProjectsForUser(username, pageable));
    }

    @PostMapping
    @Operation(
            summary = "Create a new project",
            description = "Creates a new project for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created successfully",
                    content = @Content(schema = @Schema(implementation = ProjectResult.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
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
    @Operation(
            summary = "Update project",
            description = "Updates all fields of an existing project owned by the user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated successfully",
                    content = @Content(schema = @Schema(implementation = ProjectResult.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ProjectResult> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(projectService.updateProject(id, username, request));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Patch project",
            description = "Partially updates a project owned by the user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated successfully",
                    content = @Content(schema = @Schema(implementation = ProjectResult.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ProjectResult> patchProject(@PathVariable Long id, @RequestBody ProjectRequest request, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(projectService.patchProject(id, username, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete project",
            description = """
                    Deletes a project.
                    **ADMIN only**.
                    Project must not contain active tasks.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid"),
            @ApiResponse(responseCode = "400", description = "Project contains active tasks"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        projectService.deleteProject(id, username, isAdmin);
        return ResponseEntity.noContent().build();
    }
}