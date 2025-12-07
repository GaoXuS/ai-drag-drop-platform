# API 文檔

## 基礎信息

- Base URL: `http://localhost:8080/api`
- Content-Type: `application/json`

## 模組管理 API

### 獲取所有模組

**請求**
```
GET /modules
```

**響應**
```json
[
  {
    "id": "uuid",
    "name": "模組名稱",
    "type": "模組類型",
    "description": "描述",
    "icon": "圖標",
    "version": "1.0.0",
    "config": {},
    "apiConfig": {
      "url": "https://api.example.com",
      "method": "POST",
      "timeout": 30000,
      "auth": {
        "type": "bearer",
        "credentials": {
          "token": "xxx"
        }
      },
      "headers": {
        "Content-Type": "application/json"
      }
    }
  }
]
```

### 創建模組

**請求**
```
POST /modules
Content-Type: application/json

{
  "name": "模組名稱",
  "type": "模組類型",
  "description": "描述",
  "version": "1.0.0",
  "apiConfig": {
    "url": "https://api.example.com",
    "method": "POST"
  }
}
```

## 工作流 API

### 創建工作流

**請求**
```
POST /workflows
Content-Type: application/json

{
  "name": "工作流名稱",
  "description": "描述",
  "nodes": [
    {
      "id": "node-1",
      "moduleId": "module-uuid",
      "position": { "x": 100, "y": 100 },
      "config": {}
    }
  ],
  "connections": [
    {
      "id": "conn-1",
      "fromNodeId": "node-1",
      "fromPort": "output",
      "toNodeId": "node-2",
      "toPort": "input"
    }
  ]
}
```

### 執行工作流

**請求**
```
POST /workflows/{id}/execute
Content-Type: application/json

{
  "input": "輸入數據"
}
```

**響應**
```json
{
  "id": "task-uuid",
  "workflowId": "workflow-uuid",
  "status": "PENDING",
  "startTime": null,
  "endTime": null
}
```

## 任務 API

### 獲取任務詳情

**請求**
```
GET /tasks/{id}
```

**響應**
```json
{
  "id": "task-uuid",
  "workflowId": "workflow-uuid",
  "status": "COMPLETED",
  "result": {},
  "error": null,
  "startTime": "2024-01-01T10:00:00",
  "endTime": "2024-01-01T10:01:00"
}
```

### 獲取任務日誌

**請求**
```
GET /tasks/{id}/logs
```

**響應**
```json
[
  {
    "id": "log-uuid",
    "taskId": "task-uuid",
    "moduleId": "module-uuid",
    "level": "INFO",
    "message": "開始執行工作流",
    "timestamp": "2024-01-01T10:00:00",
    "data": null
  }
]
```

## 錯誤處理

所有錯誤響應格式：

```json
{
  "error": "錯誤信息",
  "status": 400
}
```

常見錯誤碼：
- 400: 請求參數錯誤
- 404: 資源不存在
- 500: 服務器內部錯誤

