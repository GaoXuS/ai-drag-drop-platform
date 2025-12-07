-- ============================================
-- 數據庫更新腳本 - 添加項目和組件配置功能
-- 執行時間: 2025-12-07
-- ============================================

USE ai_drag_drop;

-- ============================================
-- 1. 添加項目表 (如果不存在)
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
-- 2. 更新工作流表，添加 project_id 字段
-- ============================================
-- 檢查並添加 project_id 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'ai_drag_drop' 
    AND TABLE_NAME = 'workflows' 
    AND COLUMN_NAME = 'project_id');

SET @sqlstmt := IF(@exist = 0, 
    'ALTER TABLE workflows ADD COLUMN project_id VARCHAR(36) NOT NULL DEFAULT "" COMMENT "項目ID" AFTER description',
    'SELECT "project_id 字段已存在" AS message');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加外鍵約束（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = 'ai_drag_drop' 
    AND TABLE_NAME = 'workflows' 
    AND CONSTRAINT_NAME = 'fk_workflow_project');

SET @sqlstmt := IF(@exist = 0, 
    'ALTER TABLE workflows ADD CONSTRAINT fk_workflow_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE',
    'SELECT "外鍵約束已存在" AS message');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加索引
CREATE INDEX IF NOT EXISTS idx_workflow_project_id ON workflows(project_id);

-- ============================================
-- 3. 更新 ai_modules 表，添加分類和屬性配置字段
-- ============================================
-- 添加 category 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'ai_drag_drop' 
    AND TABLE_NAME = 'ai_modules' 
    AND COLUMN_NAME = 'category');

SET @sqlstmt := IF(@exist = 0, 
    'ALTER TABLE ai_modules ADD COLUMN category VARCHAR(50) DEFAULT "basic" COMMENT "模組分類: basic(基礎組件), advanced(高級組件)" AFTER type',
    'SELECT "category 字段已存在" AS message');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 properties 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'ai_drag_drop' 
    AND TABLE_NAME = 'ai_modules' 
    AND COLUMN_NAME = 'properties');

SET @sqlstmt := IF(@exist = 0, 
    'ALTER TABLE ai_modules ADD COLUMN properties TEXT COMMENT "組件屬性配置 (JSON)" AFTER api_config',
    'SELECT "properties 字段已存在" AS message');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 category 索引
CREATE INDEX IF NOT EXISTS idx_module_category ON ai_modules(category);

-- ============================================
-- 4. 更新現有數據
-- ============================================
-- 為現有模組設置默認分類
UPDATE ai_modules SET category = 'basic' WHERE category IS NULL OR category = '';

-- 為現有工作流設置默認項目（如果沒有 project_id）
-- 注意：這需要手動處理，因為需要創建默認項目
-- UPDATE workflows SET project_id = (SELECT id FROM projects LIMIT 1) WHERE project_id IS NULL OR project_id = '';

-- ============================================
-- 5. 插入示例基礎組件
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
-- 6. 插入示例高級組件
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
SELECT '數據庫更新完成！' AS message;
SELECT '已添加項目表、工作流項目關聯、組件分類和屬性配置功能' AS summary;

