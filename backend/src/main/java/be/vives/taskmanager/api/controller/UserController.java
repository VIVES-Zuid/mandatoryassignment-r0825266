package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.UserRequest;
import be.vives.taskmanager.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Administrative user management endpoints")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/{id}/role")
    @Operation(
            summary = "Update user role",
            description = "Allows an ADMIN to change the role of a user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role updated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    public ResponseEntity<Void> updateUserRole(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        userService.updateUserRole(id, request.getRole());
        return ResponseEntity.ok().build();
    }
}