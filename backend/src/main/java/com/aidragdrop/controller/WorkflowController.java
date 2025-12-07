package com.aidragdrop.controller;

import com.aidragdrop.dto.WorkflowDTO;
import com.aidragdrop.dto.TaskDTO;
import com.aidragdrop.service.WorkflowService;
import com.aidragdrop.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkflowController {
    
    private final WorkflowService workflowService;
    private final TaskService taskService;
    
    @GetMapping
    public ResponseEntity<List<WorkflowDTO>> getAllWorkflows() {
        return ResponseEntity.ok(workflowService.getAllWorkflows());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDTO> getWorkflow(@PathVariable String id) {
        return ResponseEntity.ok(workflowService.getWorkflowById(id));
    }
    
    @PostMapping
    public ResponseEntity<WorkflowDTO> createWorkflow(@Valid @RequestBody WorkflowDTO dto) {
        return ResponseEntity.ok(workflowService.createWorkflow(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDTO> updateWorkflow(
            @PathVariable String id,
            @Valid @RequestBody WorkflowDTO dto) {
        return ResponseEntity.ok(workflowService.updateWorkflow(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable String id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<TaskDTO> executeWorkflow(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> input) {
        return ResponseEntity.ok(taskService.executeWorkflow(id, input));
    }
}

