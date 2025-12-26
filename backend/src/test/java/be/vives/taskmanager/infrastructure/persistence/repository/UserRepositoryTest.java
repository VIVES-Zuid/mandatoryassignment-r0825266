package be.vives.taskmanager.infrastructure.persistence.repository;

import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User createUser(String username, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("encoded-password");
        user.setRole(role);
        user.setEmail(username + "@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        return user;
    }

    @Test
    void findByUsername_whenExists_shouldReturnUser() {
        // Arrange
        User user = createUser("john", UserRole.USER);
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> found = userRepository.findByUsername("john");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john");
        assertThat(found.get().getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void findByUsername_whenNotExists_shouldReturnEmpty() {
        // Act
        Optional<User> found = userRepository.findByUsername("unknown");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void existsByUsername_whenExists_shouldReturnTrue() {
        // Arrange
        User user = createUser("admin", UserRole.ADMIN);
        entityManager.persistAndFlush(user);

        // Act
        boolean exists = userRepository.existsByUsername("admin");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_whenNotExists_shouldReturnFalse() {
        // Act
        boolean exists = userRepository.existsByUsername("ghost");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void save_shouldPersistUser() {
        // Arrange
        User user = createUser("save-test", UserRole.USER);

        // Act
        User saved = userRepository.save(user);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("save-test");
    }

    @Test
    void delete_shouldRemoveUser() {
        // Arrange
        User user = createUser("delete-test", UserRole.USER);
        entityManager.persistAndFlush(user);

        // Act
        userRepository.delete(user);

        // Assert
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}