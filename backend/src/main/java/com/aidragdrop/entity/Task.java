package com.aidragdrop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String workflowId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
    
    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Object result;
    
    @Column(columnDefinition = "TEXT")
    private String error;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @OneToMany(mappedBy = "taskId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskLog> logs;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum TaskStatus {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    }
}

