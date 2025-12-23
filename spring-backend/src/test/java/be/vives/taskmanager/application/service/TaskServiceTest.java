package be.vives.taskmanager.application.service;

import be.vives.taskmanager.application.dto.request.TaskRequest;
import be.vives.taskmanager.application.exception.BadRequestException;
import be.vives.taskmanager.application.exception.ResourceNotFoundException;
import be.vives.taskmanager.domain.model.Project;
import be.vives.taskmanager.domain.model.Task;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import be.vives.taskmanager.infrastructure.persistence.repository.ProjectRepository;
import be.vives.taskmanager.infrastructure.persistence.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private User user(String username) {
        User user = new User();
        user.setUsername(username);
        user.setRole(UserRole.USER);
        return user;
    }

    private Project project(Long id, User owner) {
        Project project = new Project();
        project.setId(id);
        project.setOwner(owner);
        return project;
    }

    private Task task(Long id, Project project, TaskStatus status) {
        Task task = new Task();
        task.setId(id);
        task.setProject(project);
        task.setStatus(status);
        task.setTitle("Task");
        task.setDescription("Desc");
        task.setDueDate(LocalDate.now().plusDays(1));
        return task;
    }

    // getTaskById
    @Test
    void getTaskById_whenOwner_shouldReturnTask() {
        User user = user("john");
        Project project = project(1L, user);
        Task task = task(10L, project, TaskStatus.TODO);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        var result = taskService.getTaskById(10L, "john");

        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void getTaskById_whenNotOwner_shouldThrowAccessDenied() {
        Project project = project(1L, user("owner"));
        Task task = task(10L, project, TaskStatus.TODO);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() ->
                taskService.getTaskById(10L, "other"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getTaskById_whenNotFound_shouldThrowNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                taskService.getTaskById(99L, "john"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // createTask
    @Test
    void createTask_whenOwner_shouldCreateTask() {
        User user = user("john");
        Project project = project(1L, user);

        TaskRequest request = new TaskRequest();
        request.setTitle("New task");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = taskService.createTask(1L, "john", request);

        assertThat(result.getTitle()).isEqualTo("New task");
    }

    @Test
    void createTask_whenNotOwner_shouldThrowAccessDenied() {
        Project project = project(1L, user("owner"));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        TaskRequest request = new TaskRequest();

        assertThatThrownBy(() ->
                taskService.createTask(1L, "other", request))
                .isInstanceOf(AccessDeniedException.class);
    }

    // updateTask
    @Test
    void updateTask_whenTodo_shouldUpdate() {
        User user = user("john");
        Project project = project(1L, user);
        Task task = task(10L, project, TaskStatus.TODO);

        TaskRequest request = new TaskRequest();
        request.setTitle("Updated");

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = taskService.updateTask(10L, "john", request);

        assertThat(result.getTitle()).isEqualTo("Updated");
    }

    @Test
    void updateTask_whenDone_shouldThrowBadRequest() {
        User user = user("john");
        Project project = project(1L, user);
        Task task = task(10L, project, TaskStatus.DONE);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        TaskRequest request = new TaskRequest();
        request.setTitle("Illegal");

        assertThatThrownBy(() ->
                taskService.updateTask(10L, "john", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Completed tasks cannot be modified");
    }

    // patchTask
    @Test
    void patchTask_whenDone_onlyStatusChangeAllowed() {
        User user = user("john");
        Project project = project(1L, user);
        Task task = task(10L, project, TaskStatus.DONE);

        TaskRequest request = new TaskRequest();
        request.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = taskService.patchTask(10L, "john", request);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void patchTask_whenDone_otherFields_shouldFail() {
        User user = user("john");
        Project project = project(1L, user);
        Task task = task(10L, project, TaskStatus.DONE);

        TaskRequest request = new TaskRequest();
        request.setTitle("Illegal");

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() ->
                taskService.patchTask(10L, "john", request))
                .isInstanceOf(BadRequestException.class);
    }

    // deleteTask
    @Test
    void deleteTask_whenTodoAndAdmin_shouldDelete() {
        Project project = project(1L, user("owner"));
        Task task = task(10L, project, TaskStatus.TODO);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        taskService.deleteTask(10L, "admin", true);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_whenDone_shouldThrowBadRequest() {
        Project project = project(1L, user("owner"));
        Task task = task(10L, project, TaskStatus.DONE);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() ->
                taskService.deleteTask(10L, "admin", true))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void deleteTask_whenNotOwnerAndNotAdmin_shouldThrowAccessDenied() {
        Project project = project(1L, user("owner"));
        Task task = task(10L, project, TaskStatus.TODO);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() ->
                taskService.deleteTask(10L, "other", false))
                .isInstanceOf(AccessDeniedException.class);
    }
}