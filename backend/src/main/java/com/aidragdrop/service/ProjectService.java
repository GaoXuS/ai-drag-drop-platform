package com.aidragdrop.service;

import com.aidragdrop.dto.ProjectDTO;
import com.aidragdrop.entity.Project;
import com.aidragdrop.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public ProjectDTO getProjectById(String id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("項目不存在: " + id));
        return toDTO(project);
    }
    
    @Transactional
    public ProjectDTO createProject(ProjectDTO dto) {
        Project project = toEntity(dto);
        project = projectRepository.save(project);
        return toDTO(project);
    }
    
    @Transactional
    public ProjectDTO updateProject(String id, ProjectDTO dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("項目不存在: " + id));
        
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        if (dto.getIcon() != null) {
            project.setIcon(dto.getIcon());
        }
        
        project = projectRepository.save(project);
        return toDTO(project);
    }
    
    @Transactional
    public void deleteProject(String id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("項目不存在: " + id);
        }
        projectRepository.deleteById(id);
    }
    
    private ProjectDTO toDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setIcon(project.getIcon());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        return dto;
    }
    
    private Project toEntity(ProjectDTO dto) {
        Project project = new Project();
        if (dto.getId() != null) {
            project.setId(dto.getId());
        }
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setIcon(dto.getIcon());
        return project;
    }
}

