package be.vives.taskmanager.application.service;

import be.vives.taskmanager.application.dto.request.ProjectRequest;
import be.vives.taskmanager.application.dto.result.ProjectResult;
import be.vives.taskmanager.application.exception.*;
import be.vives.taskmanager.application.mapper.ProjectMapper;
import be.vives.taskmanager.domain.model.Project;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
import be.vives.taskmanager.infrastructure.persistence.repository.ProjectRepository;
import be.vives.taskmanager.infrastructure.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public ProjectResult getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        return ProjectMapper.toResult(project);
    }

    public Page<ProjectResult> findAllProjectsForUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));

        return projectRepository.findByOwner(user, pageable)
                .map(ProjectMapper::toResult);
    }

    public ProjectResult createProject(String username, ProjectRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));

        Project project = ProjectMapper.toEntity(request);
        project.setOwner(user);

        Project saved = projectRepository.save(project);
        return ProjectMapper.toResult(saved);
    }

    public ProjectResult updateProject(Long projectId, ProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        ProjectMapper.updateEntity(project, request);
        return ProjectMapper.toResult(projectRepository.save(project));
    }

    public ProjectResult patchProject(Long projectId, ProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        return ProjectMapper.toResult(projectRepository.save(project));
    }

    public void deleteProject(Long projectId) {
        /*if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }*/

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        boolean hasOpenTasks = project.getTasks().stream()
                .anyMatch(task -> task.getStatus() != TaskStatus.DONE);

        if (hasOpenTasks) {
            throw new BadRequestException("Project contains active tasks");
        }

        projectRepository.deleteById(projectId);
    }
}