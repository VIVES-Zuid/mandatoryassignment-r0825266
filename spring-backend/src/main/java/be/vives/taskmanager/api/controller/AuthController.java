package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.LoginRequest;
import be.vives.taskmanager.application.dto.request.RegisterRequest;
import be.vives.taskmanager.application.dto.result.AuthResult;
import be.vives.taskmanager.application.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(exposedHeaders = "*")
@RequestMapping("/api/auth")
//@PermitAll
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResult> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}