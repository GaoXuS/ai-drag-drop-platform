# 開發計劃

## 項目概述

模組化 AI 拖拉拽平台開發計劃

## 開發階段

| 階段 | 時間 | 任務 | 狀態 |
|------|------|------|------|
| 需求分析 | 1 周 | 確認功能需求、模組化設計方案 | ✅ 完成 |
| 技術選型 | 1 周 | 前後端框架選擇、數據庫及緩存方案確認 | ✅ 完成 |
| 系統設計 | 2 周 | 數據庫表設計、接口定義、前端組件設計 | ✅ 完成 |
| 前端開發 | 3 周 | 拖拉拽組件開發、模組屬性編輯、界面交互 | ✅ 完成 |
| 後端開發 | 3 周 | API 調用管理、模組調度、日誌管理、緩存策略 | ✅ 完成 |
| 集成測試 | 2 周 | 前後端聯調、性能測試、安全測試 | ⏳ 進行中 |
| 部署與交付 | 1 周 | 打包、部署文檔、用戶培訓 | ⏳ 待開始 |

## 技術架構

### 前端技術棧
- Vue 3.4+
- Vite 5.x
- TypeScript 5.x
- Element Plus 2.x
- Pinia 2.x
- Vue Router 4.x
- Axios 1.x

### 後端技術棧
- Spring Boot 3.2.0
- Java 21
- Maven 3.x
- Spring Data JPA
- Spring Data Redis
- MySQL/PostgreSQL
- WebFlux (用於異步 HTTP 調用)

## 數據庫設計

### 表結構

1. **ai_modules** - AI 模組表
   - id (UUID)
   - name (VARCHAR, UNIQUE)
   - type (VARCHAR)
   - description (TEXT)
   - icon (VARCHAR)
   - version (VARCHAR)
   - config (JSON)
   - api_config (JSON)
   - created_at (TIMESTAMP)
   - updated_at (TIMESTAMP)

2. **workflows** - 工作流表
   - id (UUID)
   - name (VARCHAR)
   - description (TEXT)
   - nodes (JSON)
   - connections (JSON)
   - created_at (TIMESTAMP)
   - updated_at (TIMESTAMP)

3. **tasks** - 任務表
   - id (UUID)
   - workflow_id (UUID)
   - status (ENUM)
   - result (JSON)
   - error (TEXT)
   - start_time (TIMESTAMP)
   - end_time (TIMESTAMP)
   - created_at (TIMESTAMP)

4. **task_logs** - 任務日誌表
   - id (UUID)
   - task_id (UUID)
   - module_id (VARCHAR)
   - level (ENUM)
   - message (TEXT)
   - timestamp (TIMESTAMP)
   - data (JSON)

## API 接口設計

### 模組管理 API
- `GET /api/modules` - 獲取所有模組
- `GET /api/modules/{id}` - 獲取模組詳情
- `POST /api/modules` - 創建模組
- `PUT /api/modules/{id}` - 更新模組
- `DELETE /api/modules/{id}` - 刪除模組

### 工作流 API
- `GET /api/workflows` - 獲取所有工作流
- `GET /api/workflows/{id}` - 獲取工作流詳情
- `POST /api/workflows` - 創建工作流
- `PUT /api/workflows/{id}` - 更新工作流
- `DELETE /api/workflows/{id}` - 刪除工作流
- `POST /api/workflows/{id}/execute` - 執行工作流

### 任務 API
- `GET /api/tasks` - 獲取任務列表
- `GET /api/tasks/{id}` - 獲取任務詳情
- `GET /api/tasks/{id}/logs` - 獲取任務日誌
- `POST /api/tasks/{id}/cancel` - 取消任務

## 功能特性

### 已實現功能
- ✅ 模組管理（CRUD）
- ✅ 工作流設計（拖拉拽界面）
- ✅ 工作流執行（異步任務）
- ✅ 任務日誌記錄
- ✅ Redis 緩存支持
- ✅ 異步任務執行
- ✅ API 認證支持（Basic, Bearer, API Key）

### 待實現功能
- ⏳ 工作流節點連接驗證
- ⏳ 任務執行進度追蹤
- ⏳ 用戶權限管理
- ⏳ 工作流模板
- ⏳ 模組市場
- ⏳ 性能監控

## 部署說明

### 環境要求
- Java 21
- Maven 3.x
- Node.js 18+
- MySQL 8.0+ 或 PostgreSQL 14+
- Redis 6.0+

### 部署步驟

1. **數據庫初始化**
   ```sql
   CREATE DATABASE ai_drag_drop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **後端部署**
   ```bash
   cd backend
   mvn clean package
   java -jar target/ai-drag-drop-platform-backend-1.0.0.jar
   ```

3. **前端部署**
   ```bash
   cd frontend
   npm install
   npm run build
   # 將 dist 目錄部署到 Nginx 或其他 Web 服務器
   ```

## 風險與應對

### 已識別風險
1. **前端拖拉拽複雜性**
   - 應對：使用成熟的拖拉拽庫，充分測試兼容性

2. **用戶自定義 AI API 接口不穩定**
   - 應對：實現超時、重試、異常處理機制

3. **跨項目集成依賴衝突**
   - 應對：明確模組接口和版本管理，使用 Maven 版本控制

## 後續優化

1. 性能優化
   - 工作流執行並行化
   - 緩存策略優化
   - 數據庫查詢優化

2. 功能擴展
   - 支持更多認證方式
   - 工作流版本控制
   - 模組市場和分享功能

3. 安全性增強
   - API 限流
   - 敏感信息加密
   - 審計日誌

