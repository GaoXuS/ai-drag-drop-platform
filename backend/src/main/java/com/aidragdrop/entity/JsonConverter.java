package com.aidragdrop.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import java.util.Map;

@Converter
public class JsonConverter implements AttributeConverter<Object, String> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error converting to database column", e);
        }
    }
    
    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            // 先尝试解析为通用对象，支持Map和List
            return objectMapper.readValue(dbData, Object.class);
        } catch (Exception e) {
            // 如果解析失败，记录错误并返回null
            throw new RuntimeException("Error converting to entity attribute: " + e.getMessage(), e);
        }
    }
}

