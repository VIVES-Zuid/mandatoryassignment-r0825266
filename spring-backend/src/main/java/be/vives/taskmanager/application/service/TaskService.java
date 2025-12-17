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
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public TaskResult getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        return TaskMapper.toResult(task);
    }

    public Page<TaskResult> findAllTasksForProject(Long projectId, Pageable pageable) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }

        return taskRepository.findByProjectId(projectId, pageable)
                .map(TaskMapper::toResult);
    }

    public Page<TaskResult> findAllTasksByStatus(Long projectId, TaskStatus status, Pageable pageable) {
        return taskRepository.findByProjectIdAndStatus(projectId, status, pageable)
                .map(TaskMapper::toResult);
    }

    public TaskResult createTask(Long projectId, TaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project", projectId));

        Task task = TaskMapper.toEntity(request);
        task.setProject(project);

        Task saved = taskRepository.save(task);
        return TaskMapper.toResult(saved);
    }

    public TaskResult updateTask(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        CheckAndThrowBadRequestException(task, "Completed tasks cannot be modified");

        TaskMapper.updateEntity(task, request);
        return TaskMapper.toResult(taskRepository.save(task));
    }

    public TaskResult patchTask(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        CheckAndThrowBadRequestException(task, "Completed tasks cannot be modified");

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

    public void deleteTask(Long taskId) {
        /*if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", taskId);
        }*/
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task", taskId));
        CheckAndThrowBadRequestException(task, "Completed tasks cannot be deleted");

        taskRepository.deleteById(taskId);
    }

    private void CheckAndThrowBadRequestException (Task task, String message){
        if (task.getStatus() == TaskStatus.DONE) {
            throw new BadRequestException(message);
        }
    }
}