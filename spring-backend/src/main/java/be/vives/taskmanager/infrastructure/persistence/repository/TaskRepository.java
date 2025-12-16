package be.vives.taskmanager.infrastructure.persistence.repository;

import be.vives.taskmanager.domain.model.Task;
import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
}