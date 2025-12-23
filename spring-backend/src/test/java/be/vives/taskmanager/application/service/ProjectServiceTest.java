package be.vives.taskmanager.application.service;

import be.vives.taskmanager.application.dto.request.ProjectRequest;
import be.vives.taskmanager.application.dto.result.ProjectResult;
import be.vives.taskmanager.application.exception.BadRequestException;
import be.vives.taskmanager.application.exception.ResourceNotFoundException;
import be.vives.taskmanager.domain.model.Project;
import be.vives.taskmanager.domain.model.Task;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
import be.vives.taskmanager.infrastructure.persistence.repository.ProjectRepository;
import be.vives.taskmanager.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;


    private User user(String username) {
        User u = new User();
        u.setUsername(username);
        return u;
    }

    private Project project(Long id, User owner) {
        Project p = new Project();
        p.setId(id);
        p.setOwner(owner);
        p.setTasks(new ArrayList<>());
        return p;
    }

    //GET project by ID

    @Test
    void getProjectById_whenOwner_shouldReturnProject() {
        User owner = user("john");
        Project project = project(1L, owner);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectResult result = projectService.getProjectById(1L, "john");

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getProjectById_whenNotOwner_shouldThrowAccessDenied() {
        User owner = user("john");
        Project project = project(1L, owner);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() ->
                projectService.getProjectById(1L, "mary"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getProjectById_whenNotFound_shouldThrowException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                projectService.getProjectById(1L, "john"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    //FIND ALL projects

    @Test
    void findAllProjectsForUser_shouldReturnPagedProjects() {
        User user = user("john");
        Pageable pageable = PageRequest.of(0, 10);

        Project p1 = project(1L, user);
        Project p2 = project(2L, user);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(projectRepository.findByOwner(user, pageable))
                .thenReturn(new PageImpl<>(List.of(p1, p2)));

        Page<ProjectResult> result =
                projectService.findAllProjectsForUser("john", pageable);

        assertThat(result.getContent()).hasSize(2);
    }

    //CREATE project

    @Test
    void createProject_shouldSetOwnerAndSave() {
        User user = user("john");
        ProjectRequest request = new ProjectRequest();
        request.setName("Test");
        request.setDescription("Desc");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(projectRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProjectResult result = projectService.createProject("john", request);

        assertThat(result.getName()).isEqualTo("Test");

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        assertThat(captor.getValue().getOwner()).isEqualTo(user);
    }

    // UPDATE / PATCH

    @Test
    void updateProject_whenOwner_shouldUpdate() {
        User user = user("john");
        Project project = project(1L, user);
        ProjectRequest request = new ProjectRequest();
        request.setName("New");
        request.setDescription("New desc");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProjectResult result =
                projectService.updateProject(1L, "john", request);

        assertThat(result.getName()).isEqualTo("New");
    }

    @Test
    void patchProject_whenOwner_shouldUpdatePartial() {
        User user = user("john");
        Project project = project(1L, user);

        ProjectRequest request = new ProjectRequest();
        request.setName("Patched");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProjectResult result =
                projectService.patchProject(1L, "john", request);

        assertThat(result.getName()).isEqualTo("Patched");
    }

    @Test
    void updateProject_whenNotOwner_shouldThrowAccessDenied() {
        User owner = user("john");
        Project project = project(1L, owner);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() ->
                projectService.updateProject(1L, "mary", new ProjectRequest()))
                .isInstanceOf(AccessDeniedException.class);
    }

    // DELETE project

    @Test
    void deleteProject_whenAdmin_shouldDeleteAnyProject() {
        User owner = user("john");
        Project project = project(1L, owner);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteProject(1L, "admin", true);

        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProject_whenUserIsOwner_shouldDelete() {
        User owner = user("john");
        Project project = project(1L, owner);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteProject(1L, "john", false);

        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProject_whenUserNotOwnerAndNotAdmin_shouldThrowAccessDenied() {
        User owner = user("john");
        Project project = project(1L, owner);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() ->
                projectService.deleteProject(1L, "mary", false))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteProject_whenActiveTasksExist_shouldThrowBadRequest() {
        User owner = user("john");
        Project project = project(1L, owner);

        Task activeTask = new Task();
        activeTask.setStatus(TaskStatus.IN_PROGRESS);
        project.getTasks().add(activeTask);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() ->
                projectService.deleteProject(1L, "john", false))
                .isInstanceOf(BadRequestException.class);
    }
}