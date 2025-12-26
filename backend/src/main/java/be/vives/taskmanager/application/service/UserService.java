package be.vives.taskmanager.application.service;

import be.vives.taskmanager.application.exception.ResourceNotFoundException;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import be.vives.taskmanager.infrastructure.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateUserRole(Long userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.setRole(newRole);
        userRepository.save(user);
    }
}

