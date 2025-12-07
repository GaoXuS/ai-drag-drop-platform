-- ============================================
-- 智能体搭建平台 - 完整數據庫結構腳本
-- 數據庫: MySQL 8.0+
-- 更新日期: 2025-12-07
-- ============================================

-- 創建數據庫（如果不存在）
CREATE DATABASE IF NOT EXISTS ai_drag_drop 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用數據庫
USE ai_drag_drop;

-- ============================================
-- 1. 項目表 (projects)
-- ============================================
CREATE TABLE IF NOT EXISTS projects (
    id VARCHAR(36) PRIMARY KEY COMMENT '項目ID (UUID)',
    name VARCHAR(255) NOT NULL COMMENT '項目名稱',
    description TEXT COMMENT '項目描述',
    icon VARCHAR(500) COMMENT '項目圖標URL',
    created_at DATETIME NOT NULL COMMENT '創建時間',
    updated_at DATETIME NOT NULL COMMENT '更新時間',
    INDEX idx_name (name),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='項目表';

-- ============================================
-- 2. AI 模組表 (ai_modules)
-- ============================================
CREATE TABLE IF NOT EXISTS ai_modules (
    id VARCHAR(36) PRIMARY KEY COMMENT '模組ID (UUID)',
    name VARCHAR(255) NOT NULL UNIQUE COMMENT '模組名稱',
    type VARCHAR(255) NOT NULL COMMENT '模組類型',
    category VARCHAR(50) DEFAULT 'basic' COMMENT '模組分類: basic(基礎組件), advanced(高級組件)',
    description TEXT COMMENT '模組描述',
    icon VARCHAR(255) COMMENT '模組圖標',
    version VARCHAR(255) NOT NULL COMMENT '模組版本',
    config TEXT COMMENT '模組配置 (JSON)',
    api_config TEXT NOT NULL COMMENT 'API 配置 (JSON)',
    properties TEXT COMMENT '組件屬性配置 (JSON)',
    created_at DATETIME NOT NULL COMMENT '創建時間',
    updated_at DATETIME NOT NULL COMMENT '更新時間',
    INDEX idx_name (name),
    INDEX idx_type (type),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 模組表';

-- ============================================
-- 3. 工作流表 (workflows)
-- ============================================
CREATE TABLE IF NOT EXISTS workflows (
    id VARCHAR(36) PRIMARY KEY COMMENT '工作流ID (UUID)',
    name VARCHAR(255) NOT NULL COMMENT '工作流名稱',
    description TEXT COMMENT '工作流描述',
    project_id VARCHAR(36) NOT NULL COMMENT '項目ID',
    nodes TEXT COMMENT '節點列表 (JSON)',
    connections TEXT COMMENT '連接關係 (JSON)',
    created_at DATETIME NOT NULL COMMENT '創建時間',
    updated_at DATETIME NOT NULL COMMENT '更新時間',
    INDEX idx_name (name),
    INDEX idx_project_id (project_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流表';

-- ============================================
-- 4. 任務表 (tasks)
-- ============================================
CREATE TABLE IF NOT EXISTS tasks (
    id VARCHAR(36) PRIMARY KEY COMMENT '任務ID (UUID)',
    workflow_id VARCHAR(36) NOT NULL COMMENT '工作流ID',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '任務狀態: PENDING, RUNNING, COMPLETED, FAILED, CANCELLED',
    result TEXT COMMENT '執行結果 (JSON)',
    error TEXT COMMENT '錯誤信息',
    start_time DATETIME COMMENT '開始時間',
    end_time DATETIME COMMENT '結束時間',
    created_at DATETIME NOT NULL COMMENT '創建時間',
    INDEX idx_workflow_id (workflow_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任務表';

-- ============================================
-- 5. 任務日誌表 (task_logs)
-- ============================================
CREATE TABLE IF NOT EXISTS task_logs (
    id VARCHAR(36) PRIMARY KEY COMMENT '日誌ID (UUID)',
    task_id VARCHAR(36) NOT NULL COMMENT '任務ID',
    module_id VARCHAR(36) NOT NULL COMMENT '模組ID',
    level VARCHAR(10) NOT NULL COMMENT '日誌級別: INFO, WARN, ERROR',
    message TEXT NOT NULL COMMENT '日誌消息',
    timestamp DATETIME NOT NULL COMMENT '時間戳',
    data TEXT COMMENT '附加數據 (JSON)',
    INDEX idx_task_id (task_id),
    INDEX idx_module_id (module_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_level (level),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任務日誌表';

-- ============================================
-- 初始化數據 - 基礎組件
-- ============================================
INSERT INTO ai_modules (id, name, type, category, description, icon, version, api_config, properties, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440010', '文本輸入', 'text-input', 'basic', '用戶輸入文本信息', '📝', '1.0.0', 
 '{"url": "http://localhost:8080/api/text/input", "method": "POST", "timeout": 10000, "auth": {"type": "none"}}',
 '{"inputType": "text", "timeout": 10, "enableCache": true, "enableLog": false}',
 NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440011', '條件判斷', 'condition', 'basic', '根據條件執行不同流程', '✅', '1.0.0',
 '{"url": "http://localhost:8080/api/condition/check", "method": "POST", "timeout": 5000, "auth": {"type": "none"}}',
 '{"conditionType": "equals", "timeout": 5, "enableCache": false, "enableLog": true}',
 NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440012', 'API調用', 'api-call', 'basic', '調用外部接口獲取數據', '🔧', '1.0.0',
 '{"url": "http://localhost:8080/api/external/call", "method": "POST", "timeout": 30000, "auth": {"type": "none"}}',
 '{"method": "POST", "timeout": 30, "enableCache": true, "enableLog": true}',
 NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ============================================
-- 初始化數據 - 高級組件
-- ============================================
INSERT INTO ai_modules (id, name, type, category, description, icon, version, api_config, properties, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440020', 'AI模型', 'ai-model', 'advanced', '集成大語言模型能力', '🧠', '1.0.0',
 '{"url": "https://api.openai.com/v1/chat/completions", "method": "POST", "timeout": 60000, "auth": {"type": "bearer", "credentials": {"token": "your-api-key"}}}',
 '{"model": "gpt-4", "temperature": 0.7, "maxTokens": 2000, "enableCache": true, "enableLog": true}',
 NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440021', '數據分析', 'data-analysis', 'advanced', '數據處理與分析模組', '📊', '1.0.0',
 '{"url": "http://localhost:8080/api/analysis/process", "method": "POST", "timeout": 30000, "auth": {"type": "none"}}',
 '{"analysisType": "statistical", "timeout": 30, "enableCache": true, "enableLog": true}',
 NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440022', '插件集成', 'plugin-integration', 'advanced', '連接第三方服務', '🔌', '1.0.0',
 '{"url": "http://localhost:8080/api/plugin/connect", "method": "POST", "timeout": 20000, "auth": {"type": "none"}}',
 '{"pluginType": "third-party", "timeout": 20, "enableCache": false, "enableLog": true}',
 NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ============================================
-- 完成
-- ============================================
SELECT '數據庫結構創建完成！' AS message;
SELECT '已創建表: projects, ai_modules, workflows, tasks, task_logs' AS summary;
SELECT '已插入示例組件: 3個基礎組件 + 3個高級組件' AS components;

