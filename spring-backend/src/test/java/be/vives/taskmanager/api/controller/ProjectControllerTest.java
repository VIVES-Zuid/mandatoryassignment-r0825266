package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.ProjectRequest;
import be.vives.taskmanager.application.dto.result.ProjectResult;
import be.vives.taskmanager.application.service.ProjectService;
import be.vives.taskmanager.config.SecurityConfig;
import be.vives.taskmanager.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtService jwtService;

    //GET /api/projects/{id}
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getProjectById_ownedProject_returns200() throws Exception {
        ProjectResult result = new ProjectResult();
        result.setId(1L);
        result.setName("Test Project");
        result.setDescription("Description");

        when(projectService.getProjectById(1L, "user")).thenReturn(result);

        mockMvc.perform(get("/api/projects/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getProjectById_notOwner_returns403() throws Exception {
        when(projectService.getProjectById(1L, "user"))
                .thenThrow(new AccessDeniedException("Not owner"));

        mockMvc.perform(get("/api/projects/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    //GET /api/projects
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void findAllProjects_returnsPage() throws Exception {
        ProjectResult result = new ProjectResult();
        result.setId(1L);
        result.setName("Project");
        result.setDescription("Desc");

        Page<ProjectResult> page = new PageImpl<>(
                List.of(result),
                PageRequest.of(0, 10),
                1
        );

        when(projectService.findAllProjectsForUser(eq("user"), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Project"));
    }

    //GET /api/projects
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createProject_returns201() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setName("New Project");
        request.setDescription("Desc");

        ProjectResult result = new ProjectResult();
        result.setId(1L);
        result.setName("New Project");
        result.setDescription("Desc");

        when(projectService.createProject(eq("user"), any(ProjectRequest.class)))
                .thenReturn(result);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("New Project"));
    }

    //GET /api/projects
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateProject_returns200() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setName("Updated");
        request.setDescription("Updated Desc");

        ProjectResult result = new ProjectResult();
        result.setId(1L);
        result.setName("Updated");
        result.setDescription("Updated Desc");

        when(projectService.updateProject(eq(1L), eq("user"), any(ProjectRequest.class)))
                .thenReturn(result);

        mockMvc.perform(put("/api/projects/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    //GET /api/projects
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void patchProject_returns200() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setName("Patched");

        ProjectResult result = new ProjectResult();
        result.setId(1L);
        result.setName("Patched");
        result.setDescription("Desc");

        when(projectService.patchProject(eq(1L), eq("user"), any(ProjectRequest.class)))
                .thenReturn(result);

        mockMvc.perform(patch("/api/projects/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Patched"));
    }

    //GET /api/projects
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteProject_asAdmin_returns204() throws Exception {
        doNothing().when(projectService)
                .deleteProject(1L, "admin", true);

        mockMvc.perform(delete("/api/projects/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteProject_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/projects/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteProject_withActiveTasks_returns400() throws Exception {
        doThrow(new IllegalArgumentException("Project contains active tasks"))
                .when(projectService)
                .deleteProject(1L, "admin", true);

        mockMvc.perform(delete("/api/projects/{id}", 1L))
                .andExpect(status().isBadRequest());
    }
}