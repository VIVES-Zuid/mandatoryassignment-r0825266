package be.vives.taskmanager.application.service;

import be.vives.taskmanager.application.exception.ResourceNotFoundException;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import be.vives.taskmanager.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void updateUserRole_success() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUserRole(1L, UserRole.ADMIN);

        assertEquals(UserRole.ADMIN, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserRole_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUserRole(99L, UserRole.ADMIN));
    }
}