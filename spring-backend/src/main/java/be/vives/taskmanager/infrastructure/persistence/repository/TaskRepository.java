package be.vives.taskmanager.infrastructure.persistence.repository;

import be.vives.taskmanager.domain.model.Task;
import be.vives.taskmanager.domain.model.enumerator.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
    Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);
}