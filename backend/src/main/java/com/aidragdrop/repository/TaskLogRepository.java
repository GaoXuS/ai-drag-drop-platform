package com.aidragdrop.repository;

import com.aidragdrop.entity.TaskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskLogRepository extends JpaRepository<TaskLog, String> {
    List<TaskLog> findByTaskIdOrderByTimestampAsc(String taskId);
    List<TaskLog> findByTaskIdAndModuleId(String taskId, String moduleId);
}

