package be.vives.taskmanager.config;

import be.vives.taskmanager.domain.model.*;
import be.vives.taskmanager.domain.model.enumerator.*;
import be.vives.taskmanager.infrastructure.persistence.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        //preventx duplicate seeding
        if (userRepository.count() > 0) {
            return;
        }

        // USERS
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(UserRole.ADMIN);
        admin.setEmail("admin@taskmanager.local");
        admin.setFirstName("System");
        admin.setLastName("Admin");

        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setRole(UserRole.USER);
        user.setEmail("user@taskmanager.local");
        user.setFirstName("Demo");
        user.setLastName("User");
        userRepository.save(admin);
        userRepository.save(user);

        // PROJECT
        Project project = new Project();
        project.setName("Demo Project");
        project.setDescription("Initial demo project");
        project.setOwner(user);

        projectRepository.save(project);

        // TASKS
        Task task1 = new Task();
        task1.setTitle("Setup backend");
        task1.setDescription("Initialize Spring Boot project");
        task1.setStatus(TaskStatus.TODO);
        task1.setDueDate(LocalDate.now().plusDays(3));
        task1.setProject(project);

        Task task2 = new Task();
        task2.setTitle("Design database");
        task2.setDescription("Create entity relationships");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setDueDate(LocalDate.now().plusDays(5));
        task2.setProject(project);

        taskRepository.save(task1);
        taskRepository.save(task2);
    }
}
