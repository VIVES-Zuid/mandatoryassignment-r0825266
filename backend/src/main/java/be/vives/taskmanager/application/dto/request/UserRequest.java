package be.vives.taskmanager.application.dto.request;

import be.vives.taskmanager.domain.model.enumerator.UserRole;
import jakarta.validation.constraints.NotNull;

public class UserRequest {

    @NotNull
    private UserRole role;

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}

