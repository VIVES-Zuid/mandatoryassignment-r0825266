package be.vives.taskmanager.application.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProjectRequest {
    @NotBlank(message = "Project name must not be blank")
    @Size(
            min = 3,
            max = 100,
            message = "Project name must be between 3 and 100 characters"
    )
    private String name;

    @Size(
            max = 500,
            message = "Project description must be at most 500 characters"
    )
    private String description;


    // getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
