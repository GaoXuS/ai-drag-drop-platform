package com.aidragdrop.service;

import com.aidragdrop.dto.WorkflowDTO;
import com.aidragdrop.entity.Workflow;
import com.aidragdrop.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    
    private final WorkflowRepository workflowRepository;
    
    public List<WorkflowDTO> getAllWorkflows() {
        return workflowRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public WorkflowDTO getWorkflowById(String id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工作流不存在: " + id));
        return toDTO(workflow);
    }
    
    @Transactional
    public WorkflowDTO createWorkflow(WorkflowDTO dto) {
        Workflow workflow = toEntity(dto);
        workflow = workflowRepository.save(workflow);
        return toDTO(workflow);
    }
    
    @Transactional
    @SuppressWarnings("unchecked")
    public WorkflowDTO updateWorkflow(String id, WorkflowDTO dto) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工作流不存在: " + id));
        
        workflow.setName(dto.getName());
        workflow.setDescription(dto.getDescription());
        // 將 List<Map<String, Object>> 轉換為 List<Object>
        if (dto.getNodes() != null) {
            workflow.setNodes((List<Object>) (List<?>) dto.getNodes());
        }
        if (dto.getConnections() != null) {
            workflow.setConnections((List<Object>) (List<?>) dto.getConnections());
        }
        
        workflow = workflowRepository.save(workflow);
        return toDTO(workflow);
    }
    
    @Transactional
    public void deleteWorkflow(String id) {
        if (!workflowRepository.existsById(id)) {
            throw new RuntimeException("工作流不存在: " + id);
        }
        workflowRepository.deleteById(id);
    }
    
    @SuppressWarnings("unchecked")
    private WorkflowDTO toDTO(Workflow workflow) {
        WorkflowDTO dto = new WorkflowDTO();
        dto.setId(workflow.getId());
        dto.setName(workflow.getName());
        dto.setDescription(workflow.getDescription());
        // 安全地转换 List<Object> 到 List<Map<String, Object>>
        if (workflow.getNodes() != null) {
            dto.setNodes(workflow.getNodes().stream()
                    .map(node -> (Map<String, Object>) node)
                    .collect(Collectors.toList()));
        }
        if (workflow.getConnections() != null) {
            dto.setConnections(workflow.getConnections().stream()
                    .map(conn -> (Map<String, Object>) conn)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
    
    @SuppressWarnings("unchecked")
    private Workflow toEntity(WorkflowDTO dto) {
        Workflow workflow = new Workflow();
        if (dto.getId() != null) {
            workflow.setId(dto.getId());
        }
        workflow.setName(dto.getName());
        workflow.setDescription(dto.getDescription());
        // 將 List<Map<String, Object>> 轉換為 List<Object>
        if (dto.getNodes() != null) {
            workflow.setNodes((List<Object>) (List<?>) dto.getNodes());
        }
        if (dto.getConnections() != null) {
            workflow.setConnections((List<Object>) (List<?>) dto.getConnections());
        }
        return workflow;
    }
}

