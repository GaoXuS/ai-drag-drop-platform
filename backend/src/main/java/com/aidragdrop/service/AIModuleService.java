package com.aidragdrop.service;

import com.aidragdrop.dto.AIModuleDTO;
import com.aidragdrop.entity.AIModule;
import com.aidragdrop.repository.AIModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIModuleService {
    
    private final AIModuleRepository moduleRepository;
    
    @Cacheable(value = "modules", key = "'all'")
    public List<AIModuleDTO> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "modules", key = "#id")
    public AIModuleDTO getModuleById(String id) {
        AIModule module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("模块不存在: " + id));
        return toDTO(module);
    }
    
    @Transactional
    @CacheEvict(value = "modules", allEntries = true)
    public AIModuleDTO createModule(AIModuleDTO dto) {
        AIModule module = toEntity(dto);
        module = moduleRepository.save(module);
        return toDTO(module);
    }
    
    @Transactional
    @CacheEvict(value = "modules", allEntries = true)
    public AIModuleDTO updateModule(String id, AIModuleDTO dto) {
        AIModule module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("模块不存在: " + id));
        
        module.setName(dto.getName());
        module.setType(dto.getType());
        if (dto.getCategory() != null) {
            module.setCategory(dto.getCategory());
        }
        module.setDescription(dto.getDescription());
        module.setIcon(dto.getIcon());
        module.setVersion(dto.getVersion());
        module.setConfig(dto.getConfig());
        module.setApiConfig(dto.getApiConfig());
        module.setProperties(dto.getProperties());
        
        module = moduleRepository.save(module);
        return toDTO(module);
    }
    
    @Transactional
    @CacheEvict(value = "modules", allEntries = true)
    public void deleteModule(String id) {
        if (!moduleRepository.existsById(id)) {
            throw new RuntimeException("模組不存在: " + id);
        }
        moduleRepository.deleteById(id);
    }
    
    private AIModuleDTO toDTO(AIModule module) {
        AIModuleDTO dto = new AIModuleDTO();
        dto.setId(module.getId());
        dto.setName(module.getName());
        dto.setType(module.getType());
        dto.setCategory(module.getCategory());
        dto.setDescription(module.getDescription());
        dto.setIcon(module.getIcon());
        dto.setVersion(module.getVersion());
        dto.setConfig(module.getConfig());
        dto.setApiConfig(module.getApiConfig());
        dto.setProperties(module.getProperties());
        return dto;
    }
    
    private AIModule toEntity(AIModuleDTO dto) {
        AIModule module = new AIModule();
        if (dto.getId() != null) {
            module.setId(dto.getId());
        }
        module.setName(dto.getName());
        module.setType(dto.getType());
        if (dto.getCategory() != null) {
            module.setCategory(dto.getCategory());
        }
        module.setDescription(dto.getDescription());
        module.setIcon(dto.getIcon());
        module.setVersion(dto.getVersion());
        module.setConfig(dto.getConfig());
        module.setApiConfig(dto.getApiConfig());
        module.setProperties(dto.getProperties());
        return module;
    }
}

