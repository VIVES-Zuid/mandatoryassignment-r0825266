package be.vives.taskmanager.api.integration;

import be.vives.taskmanager.application.dto.request.ProjectRequest;
import be.vives.taskmanager.application.dto.request.TaskRequest;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import be.vives.taskmanager.infrastructure.persistence.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class TaskIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUsers() {
        createUser("user");
        createUser("intruder");
        createUser("admin");
    }

    private void createUser(String username) {
        if (userRepository.existsByUsername(username)) return;

        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    // Full life cycle (owner)
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void fullTaskLifecycle_asAdmin() throws Exception {
        Long projectId = createProject();

        TaskRequest create = new TaskRequest();
        create.setTitle("Task 1");
        create.setStatus(TaskStatus.TODO);

        MvcResult createResult = mockMvc.perform(post("/api/projects/{id}/tasks", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn();

        Long taskId = extractId(createResult);

        // READ via LIST endpoint
        mockMvc.perform(get("/api/projects/{id}/tasks", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(taskId));


        // UPDATE
        TaskRequest update = new TaskRequest();
        update.setTitle("Updated task");

        mockMvc.perform(patch("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated task"));

        // DELETE
        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());
    }

    // Completed task rule
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void completedTask_onlyStatusCanBeChanged() throws Exception {
        Long projectId = createProject();
        Long taskId = createTask(projectId, TaskStatus.DONE);

        TaskRequest invalid = new TaskRequest();
        invalid.setTitle("Not allowed");

        mockMvc.perform(patch("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        TaskRequest valid = new TaskRequest();
        valid.setStatus(TaskStatus.TODO);

        mockMvc.perform(patch("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TODO"));
    }

   //Forbidden access
    @Test
    @WithMockUser(username = "intruder", roles = "USER")
    void taskList_notOwner_returns403() throws Exception {
        Long projectId = createProject();

        mockMvc.perform(get("/api/projects/{id}/tasks", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

    }

    //invalid enum
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void invalidStatusEnum_returns400() throws Exception {
        Long projectId = createProject();

        String json = """
            { "title": "Bad", "status": "TODOOO" }
        """;

        mockMvc.perform(post("/api/projects/{id}/tasks", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    private Long createProject() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setName("Project");
        request.setDescription("Task test");

        MvcResult result = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        return extractId(result);
    }

    private Long createTask(Long projectId, TaskStatus status) throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Task");
        request.setStatus(status);

        MvcResult result = mockMvc.perform(post("/api/projects/{id}/tasks", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        return extractId(result);
    }

    private Long extractId(MvcResult result) {
        String location = result.getResponse().getHeader("Location");
        assertThat(location).isNotNull();
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }
}
