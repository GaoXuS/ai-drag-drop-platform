package com.aidragdrop.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AIModuleDTO {
    private String id;
    private String name;
    private String type;
    private String description;
    private String icon;
    private String version;
    private Map<String, Object> config;
    private Map<String, Object> apiConfig;
}

