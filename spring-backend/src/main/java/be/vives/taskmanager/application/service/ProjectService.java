package be.vives.taskmanager.application.service;

import be.vives.taskmanager.application.dto.request.ProjectRequest;
import be.vives.taskmanager.application.dto.result.ProjectResult;
import be.vives.taskmanager.application.exception.ResourceNotFoundException;
import be.vives.taskmanager.application.mapper.ProjectMapper;
import be.vives.taskmanager.domain.model.Project;
import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.infrastructure.persistence.repository.ProjectRepository;
import be.vives.taskmanager.infrastructure.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectResult> getProjectsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", username));

        return projectRepository.findByOwner(user)
                .stream()
                .map(ProjectMapper::toResult)
                .collect(Collectors.toList());
    }

    public ProjectResult createProject(String username, ProjectRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", username));

        Project project = ProjectMapper.toEntity(request);
        project.setOwner(user);

        Project saved = projectRepository.save(project);
        return ProjectMapper.toResult(saved);
    }

    public ProjectResult updateProject(Long projectId, ProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project", projectId));

        ProjectMapper.updateEntity(project, request);
        return ProjectMapper.toResult(projectRepository.save(project));
    }

    public void deleteProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }
        projectRepository.deleteById(projectId);
    }
}