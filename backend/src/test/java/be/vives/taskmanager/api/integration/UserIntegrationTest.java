package be.vives.taskmanager.api.integration;

import be.vives.taskmanager.application.dto.request.UserRequest;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import be.vives.taskmanager.infrastructure.persistence.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUsers() {
        createUser("user", UserRole.USER);
        createUser("admin", UserRole.ADMIN);
    }

    private void createUser(String username, UserRole role) {
        if (userRepository.existsByUsername(username)) return;

        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(role);
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminUpdatesUserRole_endToEnd() throws Exception {
        User user = userRepository.findByUsername("user").orElseThrow();

        UserRequest request = new UserRequest();
        request.setRole(UserRole.ADMIN);

        mockMvc.perform(patch("/api/users/{id}/role", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getRole()).isEqualTo(UserRole.ADMIN);
    }
}