package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.LoginRequest;
import be.vives.taskmanager.application.dto.request.RegisterRequest;
import be.vives.taskmanager.application.dto.result.AuthResult;
import be.vives.taskmanager.application.service.AuthService;
import be.vives.taskmanager.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;


    // REGISTER
    @Test
    void register_whenValid_shouldReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "password123", "john@test.com", "Andang", "Kloran");

        doNothing().when(authService).register(any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_whenInvalidBody_shouldReturn400() throws Exception {
        // missing password
        String invalidJson = """
                {
                  "username": "john"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }


    // LOGIN
    @Test
    void login_whenValid_shouldReturnToken() throws Exception {
        LoginRequest request = new LoginRequest("john", "password123");

        when(authService.login(any()))
                .thenReturn(new AuthResult("jwt-token"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_whenInvalidBody_shouldReturn400() throws Exception {
        String invalidJson = """
                {
                  "username": "john"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}