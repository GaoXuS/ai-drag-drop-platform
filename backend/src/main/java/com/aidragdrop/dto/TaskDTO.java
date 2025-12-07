package com.aidragdrop.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private String id;
    private String workflowId;
    private String status;
    private Object result;
    private String error;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

