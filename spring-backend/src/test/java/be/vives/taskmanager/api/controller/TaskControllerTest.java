package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.TaskRequest;
import be.vives.taskmanager.application.dto.result.TaskResult;
import be.vives.taskmanager.application.service.TaskService;
import be.vives.taskmanager.config.SecurityConfig;
import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
import be.vives.taskmanager.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtService jwtService;

    //GET /api/tasks/{id}

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getTaskById_ownedTask_returns200() throws Exception {
        TaskResult result = new TaskResult();
        result.setId(1L);
        result.setTitle("Task");
        result.setStatus(TaskStatus.TODO);

        when(taskService.getTaskById(1L, "user")).thenReturn(result);

        mockMvc.perform(get("/api/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getTaskById_notOwner_returns403() throws Exception {
        when(taskService.getTaskById(1L, "user"))
                .thenThrow(new AccessDeniedException("Not owner"));

        mockMvc.perform(get("/api/tasks/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    //POST /api/projects/{projectId}/tasks

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createTask_returns201() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("Desc");
        request.setStatus(TaskStatus.TODO);
        request.setDueDate(LocalDate.now());

        TaskResult result = new TaskResult();
        result.setId(1L);
        result.setTitle("New Task");

        when(taskService.createTask(eq(1L), eq("user"), any(TaskRequest.class)))
                .thenReturn(result);

        mockMvc.perform(post("/api/projects/{projectId}/tasks", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    //PUT /api/tasks/{id}

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateTask_returns200() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated");
        request.setDescription("Updated Desc");
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setDueDate(LocalDate.now());

        TaskResult result = new TaskResult();
        result.setId(1L);
        result.setTitle("Updated");

        when(taskService.updateTask(eq(1L), eq("user"), any(TaskRequest.class)))
                .thenReturn(result);

        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    //PATCH /api/tasks/{id}

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void patchTask_nonCompleted_returns200() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Patched");

        TaskResult result = new TaskResult();
        result.setId(1L);
        result.setTitle("Patched");

        when(taskService.patchTask(eq(1L), eq("user"), any(TaskRequest.class)))
                .thenReturn(result);

        mockMvc.perform(patch("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Patched"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void patchTask_completedTask_invalidField_returns400() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Illegal change");

        when(taskService.patchTask(eq(1L), eq("user"), any(TaskRequest.class)))
                .thenThrow(new IllegalArgumentException("Completed tasks cannot be modified"));

        mockMvc.perform(patch("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    //DELETE /api/tasks/{id}

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteTask_asAdmin_returns204() throws Exception {
        doNothing().when(taskService).deleteTask(1L, "admin", true);

        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteTask_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andExpect(status().isForbidden());
    }
}