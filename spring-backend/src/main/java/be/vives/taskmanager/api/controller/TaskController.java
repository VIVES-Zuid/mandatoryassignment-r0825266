package be.vives.taskmanager.api.controller;

import be.vives.taskmanager.application.dto.request.TaskRequest;
import be.vives.taskmanager.application.dto.result.TaskResult;
import be.vives.taskmanager.application.service.TaskService;
import be.vives.taskmanager.domain.model.enumerator.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResult> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Page<TaskResult>> findAllTasksForProject(@PathVariable Long projectId, @RequestParam(required = false) TaskStatus status, Pageable pageable) {
        if (status != null) {
            return ResponseEntity.ok(
                    taskService.findAllTasksByStatus(projectId, status, pageable)
            );
        }
        return ResponseEntity.ok(
                taskService.findAllTasksForProject(projectId, pageable)
        );
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResult> createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request) {
        TaskResult result = taskService.createTask(projectId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();

        return ResponseEntity.created(location).body(result);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResult> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<TaskResult> patchTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.patchTask(id, request));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}