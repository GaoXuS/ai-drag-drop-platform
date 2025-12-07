package com.aidragdrop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "ai_modules")
@Data
public class AIModule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String type;
    
    @Column(length = 50)
    private String category = "basic"; // basic(基礎組件), advanced(高級組件)
    
    private String description;
    
    private String icon;
    
    @Column(nullable = false)
    private String version;
    
    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> config;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> apiConfig;
    
    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> properties; // 組件屬性配置
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

