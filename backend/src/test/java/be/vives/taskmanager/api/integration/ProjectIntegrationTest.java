package be.vives.taskmanager.api.integration;

import be.vives.taskmanager.application.dto.request.ProjectRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import be.vives.taskmanager.domain.model.User;
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
class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUsers() {
        createUser("user", "USER");
        createUser("admin", "ADMIN");
        createUser("intruder", "USER");
    }

    private void createUser(String username, String role) {
        if (userRepository.existsByUsername(username)) return;

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(UserRole.valueOf(role));
        user.setEmail(username + "@test.com");
        user.setFirstName(username);
        user.setLastName("Test");

        userRepository.save(user);
    }

    //FULL CRUD FLOW (OWNER)
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void fullProjectLifecycle_asOwner() throws Exception {
        // CREATE
        ProjectRequest createRequest = new ProjectRequest();
        createRequest.setName("Integration Project");
        createRequest.setDescription("Created via integration test");

        MvcResult createResult = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        Long projectId = extractId(createResult);

        // READ
        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Project"));

        // UPDATE
        createRequest.setName("Updated Project");

        mockMvc.perform(put("/api/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"));

        // DELETE → USER NOT ALLOWED
        mockMvc.perform(delete("/api/projects/{id}", projectId))
                .andExpect(status().isForbidden());
    }

    //DELETE AS ADMIN

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteProject_asAdmin_returns204() throws Exception {
        Long projectId = createProjectAsUser();

        mockMvc.perform(delete("/api/projects/{id}", projectId))
                .andExpect(status().isNoContent());

        // VERIFY DELETED
        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isNotFound());
    }

    //FORBIDDEN ACCESS
    @Test
    @WithMockUser(username = "intruder", roles = "USER")
    void getProject_notOwner_returns403() throws Exception {
        Long projectId = createProjectAsUser();

        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isForbidden());
    }

    //VALIDATION
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createProject_invalidRequest_returns400() throws Exception {
        ProjectRequest invalid = new ProjectRequest();
        invalid.setName(""); // invalid
        invalid.setDescription("desc");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    private Long createProjectAsUser() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setName("Owned Project");
        request.setDescription("Owner test");

        MvcResult result = mockMvc.perform(post("/api/projects")
                        .with(user("user").roles("USER"))
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