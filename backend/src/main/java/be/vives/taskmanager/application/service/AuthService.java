package be.vives.taskmanager.application.service;

import be.vives.taskmanager.application.dto.request.LoginRequest;
import be.vives.taskmanager.application.dto.request.RegisterRequest;
import be.vives.taskmanager.application.dto.result.AuthResult;
import be.vives.taskmanager.domain.model.enumerator.*;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.infrastructure.persistence.repository.UserRepository;
import be.vives.taskmanager.infrastructure.security.JwtService;
import be.vives.taskmanager.application.exception.ConflictException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        userRepository.save(user);
    }

    public AuthResult login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new AuthResult(token);
    }
}