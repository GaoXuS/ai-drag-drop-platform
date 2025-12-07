package com.aidragdrop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> response = new HashMap<>();
        String errorMessage = e.getMessage();
        
        // 如果是JsonConverter相关的错误，提供更友好的提示
        if (errorMessage != null && errorMessage.contains("Error converting")) {
            response.put("error", "数据格式错误: " + errorMessage);
            response.put("message", "请检查JSON数据格式是否正确");
        } else {
            response.put("error", errorMessage);
        }
        
        response.put("status", HttpStatus.BAD_REQUEST.value());
        
        // 开发环境下返回堆栈跟踪
        if (System.getProperty("spring.profiles.active", "").contains("dev")) {
            response.put("stackTrace", e.getStackTrace());
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "服务器内部错误");
        response.put("message", e.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

