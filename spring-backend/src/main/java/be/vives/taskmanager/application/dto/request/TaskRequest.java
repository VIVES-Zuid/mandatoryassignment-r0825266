package be.vives.taskmanager.application.dto.request;

import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TaskRequest {

    @NotBlank(message = "Task title must not be blank")
    @Size(
            min = 3,
            max = 150,
            message = "Task title must be between 3 and 150 characters"
    )
    private String title;

    @Size(
            max = 1000,
            message = "Task description must be at most 1000 characters"
    )
    private String description;

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;

    // getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
