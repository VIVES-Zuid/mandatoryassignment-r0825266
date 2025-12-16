package be.vives.taskmanager.application.mapper;

import be.vives.taskmanager.application.dto.request.ProjectRequest;
import be.vives.taskmanager.application.dto.result.ProjectResult;
import be.vives.taskmanager.application.dto.result.TaskResult;
import be.vives.taskmanager.domain.model.Project;
import be.vives.taskmanager.domain.model.Task;

import java.util.List;
import java.util.stream.Collectors;


public class ProjectMapper {
    private ProjectMapper() {

    }

    //Maps a ProjectRequest to a Project entity. The owner is set separately in the service layer.
    public static Project toEntity(ProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return project;
    }

    //Updates an existing Project entity from a ProjectRequest.
    public static void updateEntity(Project project, ProjectRequest request) {
        project.setName(request.getName());
        project.setDescription(request.getDescription());
    }

    //Maps a Project entity to a ProjectResult DTO
    public static ProjectResult toResult(Project project) {
        ProjectResult result = new ProjectResult();
        result.setId(project.getId());
        result.setName(project.getName());
        result.setDescription(project.getDescription());
        result.setCreatedAt(project.getCreatedAt());

        if (project.getTasks() != null) {
            List<TaskResult> tasks = project.getTasks()
                    .stream()
                    .map(TaskMapper::toResult)
                    .collect(Collectors.toList());
            result.setTasks(tasks);
        }

        return result;
    }
}
