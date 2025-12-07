package com.aidragdrop.controller;

import com.aidragdrop.dto.TaskDTO;
import com.aidragdrop.entity.TaskLog;
import com.aidragdrop.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskService taskService;
    
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getTasks(
            @RequestParam(required = false) String workflowId) {
        return ResponseEntity.ok(taskService.getTasks(workflowId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }
    
    @GetMapping("/{id}/logs")
    public ResponseEntity<List<TaskLog>> getTaskLogs(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTaskLogs(id));
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelTask(@PathVariable String id) {
        taskService.cancelTask(id);
        return ResponseEntity.noContent().build();
    }
}

