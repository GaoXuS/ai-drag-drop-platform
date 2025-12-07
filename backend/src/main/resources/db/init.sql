-- ============================================
-- 模組化 AI 拖拉拽平台 - 初始化腳本
-- 此腳本僅創建數據庫，不創建表結構
-- 表結構由 JPA 自動創建（ddl-auto: update）
-- ============================================

-- 創建數據庫（如果不存在）
CREATE DATABASE IF NOT EXISTS ai_drag_drop 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用數據庫
USE ai_drag_drop;

-- 顯示數據庫信息
SELECT 
    '數據庫 ai_drag_drop 已創建' AS message,
    DATABASE() AS current_database,
    @@character_set_database AS character_set,
    @@collation_database AS collation;

