package be.vives.taskmanager.infrastructure.persistence.repository;

import be.vives.taskmanager.domain.model.Project;
import be.vives.taskmanager.domain.model.Task;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private Project createProject() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("pw");
        user.setRole(UserRole.USER);
        user.setFirstName("Andang");
        user.setLastName("Kloran");
        user.setEmail("user@example.com");
        entityManager.persist(user);

        Project project = new Project();
        project.setName("Demo Project");
        project.setOwner(user);
        return entityManager.persist(project);
    }

    @Test
    void findByProjectId_shouldReturnTasks() {
        // Arrange
        Project project = createProject();

        Task t1 = new Task();
        t1.setTitle("Task 1");
        t1.setStatus(TaskStatus.TODO);
        t1.setProject(project);

        Task t2 = new Task();
        t2.setTitle("Task 2");
        t2.setStatus(TaskStatus.IN_PROGRESS);
        t2.setProject(project);

        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.flush();

        // Act
        Page<Task> result = taskRepository.findByProjectId(project.getId(), PageRequest.of(0, 10));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByProjectIdAndStatus_shouldFilterByStatus() {
        // Arrange
        Project project = createProject();

        Task todo = new Task();
        todo.setTitle("Todo");
        todo.setStatus(TaskStatus.TODO);
        todo.setProject(project);

        Task done = new Task();
        done.setTitle("Done");
        done.setStatus(TaskStatus.DONE);
        done.setProject(project);

        entityManager.persist(todo);
        entityManager.persist(done);
        entityManager.flush();

        // Act
        Page<Task> result = taskRepository.findByProjectIdAndStatus(
                project.getId(),
                TaskStatus.TODO,
                PageRequest.of(0, 10)
        );

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Todo");
    }
}