package com.aidragdrop.controller;

import com.aidragdrop.dto.AIModuleDTO;
import com.aidragdrop.service.AIModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/modules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AIModuleController {
    
    private final AIModuleService moduleService;
    
    @GetMapping
    public ResponseEntity<List<AIModuleDTO>> getAllModules() {
        return ResponseEntity.ok(moduleService.getAllModules());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AIModuleDTO> getModule(@PathVariable String id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }
    
    @PostMapping
    public ResponseEntity<AIModuleDTO> createModule(@Valid @RequestBody AIModuleDTO dto) {
        return ResponseEntity.ok(moduleService.createModule(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AIModuleDTO> updateModule(
            @PathVariable String id,
            @Valid @RequestBody AIModuleDTO dto) {
        return ResponseEntity.ok(moduleService.updateModule(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable String id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }
}

