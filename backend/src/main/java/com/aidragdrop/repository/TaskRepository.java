package com.aidragdrop.repository;

import com.aidragdrop.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByWorkflowId(String workflowId);
    List<Task> findByStatus(Task.TaskStatus status);
}

