package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.UserRequest;
import be.vives.taskmanager.application.service.UserService;
import be.vives.taskmanager.config.SecurityConfig;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import be.vives.taskmanager.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanUpdateUserRole_returns200() throws Exception {
        UserRequest request = new UserRequest();
        request.setRole(UserRole.ADMIN);

        doNothing().when(userService).updateUserRole(eq(1L), eq(UserRole.ADMIN));

        mockMvc.perform(patch("/api/users/{id}/role", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void userCannotUpdateUserRole_returns403() throws Exception {
        UserRequest request = new UserRequest();
        request.setRole(UserRole.ADMIN);

        mockMvc.perform(patch("/api/users/{id}/role", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}