package be.vives.taskmanager.infrastructure.persistence.repository;

import be.vives.taskmanager.domain.model.Project;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("pw");
        user.setRole(UserRole.USER);
        user.setFirstName("Andang");
        user.setLastName("Kloran");
        user.setEmail(username + "@example.com");
        return entityManager.persist(user);
    }

    @Test
    void findByOwner_shouldReturnProjectsForUser() {
        // Arrange
        User user = createUser("owner");
        User other = createUser("other");

        Project p1 = new Project();
        p1.setName("Project 1");
        p1.setOwner(user);

        Project p2 = new Project();
        p2.setName("Project 2");
        p2.setOwner(user);

        Project p3 = new Project();
        p3.setName("Other Project");
        p3.setOwner(other);

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.persist(p3);
        entityManager.flush();

        // Act
        Page<Project> result = projectRepository.findByOwner(user, PageRequest.of(0, 10));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Project::getName)
                .containsExactlyInAnyOrder("Project 1", "Project 2");
    }
}