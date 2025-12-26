package be.vives.taskmanager.application.mapper;

import be.vives.taskmanager.application.dto.request.TaskRequest;
import be.vives.taskmanager.application.dto.result.TaskResult;
import be.vives.taskmanager.domain.model.Task;

public class TaskMapper {

    private TaskMapper() {

    }

    //Maps a TaskRequest to a Task entity. The project is set separately in the service layer
    public static Task toEntity(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        return task;
    }

    //Updates an existing Task entity from a TaskRequest.
    public static void updateEntity(Task task, TaskRequest request) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
    }

    //Maps a Task entity to a TaskResult DTO.
    public static TaskResult toResult(Task task) {
        TaskResult result = new TaskResult();
        result.setId(task.getId());
        result.setTitle(task.getTitle());
        result.setDescription(task.getDescription());
        result.setStatus(task.getStatus());
        result.setDueDate(task.getDueDate());
        return result;
    }
}
