package be.vives.taskmanager.application.service;

import be.vives.taskmanager.application.dto.request.TaskRequest;
import be.vives.taskmanager.application.dto.result.TaskResult;
import be.vives.taskmanager.application.exception.*;
import be.vives.taskmanager.application.mapper.TaskMapper;
import be.vives.taskmanager.domain.model.*;
import be.vives.taskmanager.domain.model.enumerator.*;
import be.vives.taskmanager.infrastructure.persistence.repository.ProjectRepository;
import be.vives.taskmanager.infrastructure.persistence.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public TaskResult getTaskById(Long taskId, String username) {
        Task task = getOwnedTask(taskId, username);
        return TaskMapper.toResult(task);
    }

    public Page<TaskResult> findAllTasksForProject(Long projectId, Pageable pageable, String username) {
        Project project = getOwnedProject(projectId, username);

        return taskRepository.findByProjectId(project.getId(), pageable)
                .map(TaskMapper::toResult);
    }

    public Page<TaskResult> findAllTasksByStatus(Long projectId, TaskStatus status, Pageable pageable, String username) {
        Project project = getOwnedProject(projectId, username);

        return taskRepository.findByProjectIdAndStatus(project.getId(), status, pageable)
                .map(TaskMapper::toResult);
    }

    public TaskResult createTask(Long projectId, String username, TaskRequest request) {
        Project project = getOwnedProject(projectId, username);

        Task task = TaskMapper.toEntity(request);
        task.setProject(project);

        return TaskMapper.toResult(taskRepository.save(task));
    }

    public TaskResult updateTask(Long taskId, String username, TaskRequest request) {
        Task task = getOwnedTask(taskId, username);

        checkIfTaskCompleted(task, "Completed tasks cannot be modified");

        TaskMapper.updateEntity(task, request);
        return TaskMapper.toResult(taskRepository.save(task));
    }

    public TaskResult patchTask(Long taskId, String username, TaskRequest request) {
        Task task = getOwnedTask(taskId, username);

        //For completed/DONE tasks, allow PATCH only if the only field being changed is status
        boolean isDone = task.getStatus() == TaskStatus.DONE;

        if (isDone) {
            boolean isTryingToChangeOtherFields = request.getTitle() != null || request.getDescription() != null || request.getDueDate() != null;

            if (isTryingToChangeOtherFields) {
                throw new BadRequestException("Completed tasks cannot be modified");
            }

            if (request.getStatus() == null) {
                throw new BadRequestException("Completed tasks cannot be modified");
            }

            task.setStatus(request.getStatus());

            return TaskMapper.toResult(taskRepository.save(task));
        }

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        return TaskMapper.toResult(taskRepository.save(task));
    }


    /*public TaskResult patchTask(Long taskId, String username, TaskRequest request) {
        Task task = getOwnedTask(taskId, username);

        checkIfTaskCompleted(task, "Completed tasks cannot be modified");

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        return TaskMapper.toResult(taskRepository.save(task));
    }*/

    public void deleteTask(Long taskId, String username, boolean isAdmin) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        // ownership check only if not admin
        if (!isAdmin && !task.getProject().getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("Not owner of task");
        }

        checkIfTaskCompleted(task, "Completed tasks cannot be deleted");

        taskRepository.delete(task);
    }

    /*public void deleteTask(Long taskId, String username) {
        Task task = getOwnedTask(taskId, username);

        checkIfTaskCompleted(task, "Completed tasks cannot be deleted");

        taskRepository.delete(task);
    }*/

    private Task getOwnedTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        String ownerUsername = task.getProject().getOwner().getUsername();
        if (!ownerUsername.equals(username)) {
            throw new AccessDeniedException("Not owner of task");
        }

        return task;
    }

    private Project getOwnedProject(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        if (!project.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("Not owner of project");
        }

        return project;
    }

    private void checkIfTaskCompleted (Task task, String message){
        if (task.getStatus() == TaskStatus.DONE) {
            throw new BadRequestException(message);
        }
    }
}