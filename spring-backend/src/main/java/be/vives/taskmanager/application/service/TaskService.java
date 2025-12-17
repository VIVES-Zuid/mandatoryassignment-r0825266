package be.vives.taskmanager.application.service;

import be.vives.taskmanager.application.dto.request.TaskRequest;
import be.vives.taskmanager.application.dto.result.TaskResult;
import be.vives.taskmanager.application.exception.ResourceNotFoundException;
import be.vives.taskmanager.application.mapper.TaskMapper;
import be.vives.taskmanager.domain.model.*;
import be.vives.taskmanager.domain.model.enumerator.*;
import be.vives.taskmanager.infrastructure.persistence.repository.ProjectRepository;
import be.vives.taskmanager.infrastructure.persistence.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public List<TaskResult> getTasksForProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(TaskMapper::toResult)
                .collect(Collectors.toList());
    }

    public List<TaskResult> getTasksByStatus(Long projectId, TaskStatus status) {
        return taskRepository.findByProjectIdAndStatus(projectId, status)
                .stream()
                .map(TaskMapper::toResult)
                .collect(Collectors.toList());
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
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task", taskId));

        TaskMapper.updateEntity(task, request);
        return TaskMapper.toResult(taskRepository.save(task));
    }

    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", taskId);
        }
        taskRepository.deleteById(taskId);
    }
}