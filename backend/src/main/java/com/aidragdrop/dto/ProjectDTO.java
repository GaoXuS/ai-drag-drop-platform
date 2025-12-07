package com.aidragdrop.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProjectDTO {
    private String id;
    private String name;
    private String description;
    private String icon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

