package be.vives.taskmanager.application.service;

import be.vives.taskmanager.application.dto.request.LoginRequest;
import be.vives.taskmanager.application.dto.request.RegisterRequest;
import be.vives.taskmanager.application.dto.result.AuthResult;
import be.vives.taskmanager.application.exception.ConflictException;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import be.vives.taskmanager.infrastructure.persistence.repository.UserRepository;
import be.vives.taskmanager.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_whenUsernameDoesNotExist_shouldSaveUser() {
        // Arrange
        String username = "kloranAwah";
        String password = "secret";

        RegisterRequest request =
                new RegisterRequest(username, password, "kloran@test.com", "Andang", "Kloran");

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encoded");

        // Act
        authService.register(request);

        // Assert
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo(username);
        assertThat(saved.getPassword()).isEqualTo("encoded");
        assertThat(saved.getRole()).isEqualTo(UserRole.USER);
        assertThat(saved.getEmail()).isEqualTo("kloran@test.com");
        assertThat(saved.getFirstName()).isEqualTo("Andang");
        assertThat(saved.getLastName()).isEqualTo("Kloran");
    }

    @Test
    void register_whenUsernameExists_shouldThrowConflictException() {
        // Arrange
        String username = "user";

        RegisterRequest request =
                new RegisterRequest(username, "user123", "user@test.com", "Andang", "Kloran");

        when(userRepository.existsByUsername(username)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_whenCredentialsAreValid_shouldReturnToken() {
        // Arrange
        String username = "user";
        String rawPassword = "user123";
        String encodedPassword = "encoded";

        LoginRequest request = new LoginRequest(username, rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRole(UserRole.USER);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);
        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        // Act
        AuthResult result = authService.login(request);

        // Assert
        assertThat(result.token()).isEqualTo("jwt-token");
    }

    @Test
    void login_whenPasswordIsWrong_shouldThrowBadCredentials() {
        // Arrange
        String username = "john";

        LoginRequest request = new LoginRequest(username, "wrong");

        User user = new User();
        user.setUsername(username);
        user.setPassword("encoded");

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
