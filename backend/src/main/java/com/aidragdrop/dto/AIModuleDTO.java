package com.aidragdrop.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AIModuleDTO {
    private String id;
    private String name;
    private String type;
    private String category; // basic(基礎組件), advanced(高級組件)
    private String description;
    private String icon;
    private String version;
    private Map<String, Object> config;
    private Map<String, Object> apiConfig;
    private Map<String, Object> properties; // 組件屬性配置
}

