package com.aidragdrop.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class WorkflowDTO {
    private String id;
    private String name;
    private String description;
    private String projectId;
    private List<Map<String, Object>> nodes;
    private List<Map<String, Object>> connections;
}

