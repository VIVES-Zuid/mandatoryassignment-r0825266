package be.vives.taskmanager.infrastructure.persistence.repository;

import be.vives.taskmanager.domain.model.Project;
import be.vives.taskmanager.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwner(User owner);

    List<Project> findByNameContainingIgnoreCase(String name);
}