-- ============================================
-- 模組化 AI 拖拉拽平台 - 數據庫結構腳本
-- 數據庫: MySQL 8.0+
-- ============================================

-- 創建數據庫（如果不存在）
CREATE DATABASE IF NOT EXISTS ai_drag_drop 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用數據庫
USE ai_drag_drop;

-- ============================================
-- 1. AI 模組表 (ai_modules)
-- ============================================
CREATE TABLE IF NOT EXISTS ai_modules (
    id VARCHAR(36) PRIMARY KEY COMMENT '模組ID (UUID)',
    name VARCHAR(255) NOT NULL UNIQUE COMMENT '模組名稱',
    type VARCHAR(255) NOT NULL COMMENT '模組類型',
    description TEXT COMMENT '模組描述',
    icon VARCHAR(255) COMMENT '模組圖標',
    version VARCHAR(255) NOT NULL COMMENT '模組版本',
    config TEXT COMMENT '模組配置 (JSON)',
    api_config TEXT NOT NULL COMMENT 'API 配置 (JSON)',
    created_at DATETIME NOT NULL COMMENT '創建時間',
    updated_at DATETIME NOT NULL COMMENT '更新時間',
    INDEX idx_name (name),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 模組表';

-- ============================================
-- 2. 工作流表 (workflows)
-- ============================================
CREATE TABLE IF NOT EXISTS workflows (
    id VARCHAR(36) PRIMARY KEY COMMENT '工作流ID (UUID)',
    name VARCHAR(255) NOT NULL COMMENT '工作流名稱',
    description TEXT COMMENT '工作流描述',
    nodes TEXT COMMENT '節點列表 (JSON)',
    connections TEXT COMMENT '連接關係 (JSON)',
    created_at DATETIME NOT NULL COMMENT '創建時間',
    updated_at DATETIME NOT NULL COMMENT '更新時間',
    INDEX idx_name (name),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流表';

-- ============================================
-- 3. 任務表 (tasks)
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
-- 4. 任務日誌表 (task_logs)
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
-- 初始化數據（可選）
-- ============================================

-- 插入示例 AI 模組（可選）
INSERT INTO ai_modules (id, name, type, description, version, api_config, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'OpenAI GPT', 'text-generation', 'OpenAI GPT 文本生成模組', '1.0.0', 
 '{"url": "https://api.openai.com/v1/chat/completions", "method": "POST", "timeout": 30000, "auth": {"type": "bearer", "credentials": {"token": "your-api-key"}}, "headers": {"Content-Type": "application/json"}}',
 NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002', '文本處理', 'text-processing', '基礎文本處理模組', '1.0.0',
 '{"url": "http://localhost:8080/api/text/process", "method": "POST", "timeout": 10000, "auth": {"type": "none"}, "headers": {"Content-Type": "application/json"}}',
 NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ============================================
-- 完成
-- ============================================
SELECT '數據庫結構創建完成！' AS message;

