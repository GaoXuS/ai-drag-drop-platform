package com.aidragdrop.service;

import com.aidragdrop.dto.TaskDTO;
import com.aidragdrop.entity.Task;
import com.aidragdrop.entity.TaskLog;
import com.aidragdrop.repository.TaskRepository;
import com.aidragdrop.repository.TaskLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final TaskLogRepository taskLogRepository;
    private final WorkflowExecutionService executionService;
    
    public TaskDTO getTaskById(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在: " + id));
        return toDTO(task);
    }
    
    public List<TaskDTO> getTasks(String workflowId) {
        List<Task> tasks;
        if (workflowId != null && !workflowId.isEmpty()) {
            tasks = taskRepository.findByWorkflowId(workflowId);
        } else {
            tasks = taskRepository.findAll();
        }
        return tasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Async
    @Transactional
    public TaskDTO executeWorkflow(String workflowId, Object input) {
        Task task = new Task();
        task.setWorkflowId(workflowId);
        task.setStatus(Task.TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task = taskRepository.save(task);
        
        // 异步执行工作流
        executionService.executeWorkflowAsync(task, input);
        
        return toDTO(task);
    }
    
    public List<TaskLog> getTaskLogs(String taskId) {
        return taskLogRepository.findByTaskIdOrderByTimestampAsc(taskId);
    }
    
    @Transactional
    public void cancelTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在: " + id));
        
        if (task.getStatus() == Task.TaskStatus.RUNNING) {
            task.setStatus(Task.TaskStatus.CANCELLED);
            task.setEndTime(LocalDateTime.now());
            taskRepository.save(task);
        }
    }
    
    private TaskDTO toDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setWorkflowId(task.getWorkflowId());
        dto.setStatus(task.getStatus().name());
        dto.setResult(task.getResult());
        dto.setError(task.getError());
        dto.setStartTime(task.getStartTime());
        dto.setEndTime(task.getEndTime());
        return dto;
    }
}

