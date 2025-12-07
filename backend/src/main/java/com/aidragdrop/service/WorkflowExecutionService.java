package com.aidragdrop.service;

import com.aidragdrop.entity.Task;
import com.aidragdrop.entity.TaskLog;
import com.aidragdrop.repository.TaskRepository;
import com.aidragdrop.repository.TaskLogRepository;
import com.aidragdrop.repository.WorkflowRepository;
import com.aidragdrop.repository.AIModuleRepository;
import com.aidragdrop.entity.Workflow;
import com.aidragdrop.entity.AIModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WorkflowExecutionService {
    
    private final TaskRepository taskRepository;
    private final TaskLogRepository taskLogRepository;
    private final WorkflowRepository workflowRepository;
    private final AIModuleRepository moduleRepository;
    
    @Value("${app.api-timeout:30000}")
    private long apiTimeout;
    
    public WorkflowExecutionService(
            TaskRepository taskRepository,
            TaskLogRepository taskLogRepository,
            WorkflowRepository workflowRepository,
            AIModuleRepository moduleRepository) {
        this.taskRepository = taskRepository;
        this.taskLogRepository = taskLogRepository;
        this.workflowRepository = workflowRepository;
        this.moduleRepository = moduleRepository;
    }
    
    @Async
    @Transactional
    public void executeWorkflowAsync(Task task, Object input) {
        try {
            task.setStatus(Task.TaskStatus.RUNNING);
            task.setStartTime(LocalDateTime.now());
            taskRepository.save(task);
            
            addLog(task.getId(), null, TaskLog.LogLevel.INFO, "开始执行工作流");
            
            Workflow workflow = workflowRepository.findById(task.getWorkflowId())
                    .orElseThrow(() -> new RuntimeException("工作流不存在"));
            
            // 执行工作流节点
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> nodes = workflow.getNodes() != null 
                    ? workflow.getNodes().stream()
                            .map(node -> (Map<String, Object>) node)
                            .collect(java.util.stream.Collectors.toList())
                    : List.of();
            Object result = input;
            
            for (Map<String, Object> nodeData : nodes) {
                String moduleId = (String) nodeData.get("moduleId");
                AIModule module = moduleRepository.findById(moduleId)
                        .orElseThrow(() -> new RuntimeException("模块不存在: " + moduleId));
                
                addLog(task.getId(), moduleId, TaskLog.LogLevel.INFO, "执行模块: " + module.getName());
                
                // 调用 AI API
                result = callAIModule(module, result, task.getId());
                
                if (task.getStatus() == Task.TaskStatus.CANCELLED) {
                    addLog(task.getId(), moduleId, TaskLog.LogLevel.WARN, "任务已取消");
                    return;
                }
            }
            
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setResult(result);
            task.setEndTime(LocalDateTime.now());
            taskRepository.save(task);
            
            addLog(task.getId(), null, TaskLog.LogLevel.INFO, "工作流执行完成");
            
        } catch (Exception e) {
            log.error("执行工作流失败", e);
            task.setStatus(Task.TaskStatus.FAILED);
            task.setError(e.getMessage());
            task.setEndTime(LocalDateTime.now());
            taskRepository.save(task);
            
            addLog(task.getId(), null, TaskLog.LogLevel.ERROR, "执行失败: " + e.getMessage());
        }
    }
    
    private Object callAIModule(AIModule module, Object input, String taskId) {
        try {
            Map<String, Object> apiConfig = module.getApiConfig();
            String url = (String) apiConfig.get("url");
            String method = (String) apiConfig.getOrDefault("method", "POST");
            
            WebClient.Builder clientBuilder = WebClient.builder()
                    .baseUrl(url)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            
            // 设置认证
            setAuthHeaders(clientBuilder, apiConfig);
            
            // 设置自定义请求头
            if (apiConfig.containsKey("headers")) {
                @SuppressWarnings("unchecked")
                Map<String, String> headers = (Map<String, String>) apiConfig.get("headers");
                headers.forEach(clientBuilder::defaultHeader);
            }
            
            WebClient webClient = clientBuilder.build();
            
            Mono<Object> response;
            if ("GET".equals(method)) {
                response = webClient.get()
                        .retrieve()
                        .bodyToMono(Object.class);
            } else {
                response = webClient.post()
                        .bodyValue(input)
                        .retrieve()
                        .bodyToMono(Object.class);
            }
            
            Object result = response
                    .timeout(Duration.ofMillis(apiTimeout))
                    .block();
            
            addLog(taskId, module.getId(), TaskLog.LogLevel.INFO, "模块调用成功");
            return result;
            
        } catch (Exception e) {
            addLog(taskId, module.getId(), TaskLog.LogLevel.ERROR, "模块调用失败: " + e.getMessage());
            throw new RuntimeException("调用 AI 模块失败: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void setAuthHeaders(WebClient.Builder builder, Map<String, Object> apiConfig) {
        if (!apiConfig.containsKey("auth")) {
            return;
        }
        
        Map<String, Object> auth = (Map<String, Object>) apiConfig.get("auth");
        String type = (String) auth.getOrDefault("type", "none");
        
        if ("bearer".equals(type)) {
            Map<String, String> credentials = (Map<String, String>) auth.get("credentials");
            String token = credentials.get("token");
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        } else if ("basic".equals(type)) {
            Map<String, String> credentials = (Map<String, String>) auth.get("credentials");
            String username = credentials.get("username");
            String password = credentials.get("password");
            // Basic Auth 需要 Base64 编码
            String authHeader = java.util.Base64.getEncoder()
                    .encodeToString((username + ":" + password).getBytes());
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + authHeader);
        } else if ("apiKey".equals(type)) {
            Map<String, String> credentials = (Map<String, String>) auth.get("credentials");
            String key = credentials.get("key");
            String value = credentials.get("value");
            builder.defaultHeader(key, value);
        }
    }
    
    private void addLog(String taskId, String moduleId, TaskLog.LogLevel level, String message) {
        TaskLog log = new TaskLog();
        log.setTaskId(taskId);
        log.setModuleId(moduleId != null ? moduleId : "");
        log.setLevel(level);
        log.setMessage(message);
        log.setTimestamp(LocalDateTime.now());
        taskLogRepository.save(log);
    }
}

