# 数据库脚本说明

## 文件说明

### 1. `schema.sql` - 完整数据库结构脚本
**用途**: 全新安装时使用，创建所有表结构
**执行方式**: 
```bash
mysql -u root -p < schema.sql
```

### 2. `update_schema.sql` - 数据库更新脚本
**用途**: 已有数据库时使用，添加新表和字段
**执行方式**: 
```bash
mysql -u root -p ai_drag_drop < update_schema.sql
```

### 3. `complete_schema.sql` - 完整结构脚本（包含示例数据）
**用途**: 全新安装时使用，包含所有表结构和示例数据
**执行方式**: 
```bash
mysql -u root -p < complete_schema.sql
```

## 数据库变更说明

### 新增表

1. **projects (项目表)**
   - `id`: 项目ID (UUID)
   - `name`: 项目名称
   - `description`: 项目描述
   - `icon`: 项目图标URL
   - `created_at`: 创建时间
   - `updated_at`: 更新时间

### 修改表

1. **workflows (工作流表)**
   - 新增字段: `project_id` (VARCHAR(36), NOT NULL)
   - 新增外键: `fk_workflow_project` 关联 `projects(id)`
   - 新增索引: `idx_workflow_project_id`

2. **ai_modules (AI模块表)**
   - 新增字段: `category` (VARCHAR(50), DEFAULT 'basic')
     - 值: 'basic' (基础组件) 或 'advanced' (高级组件)
   - 新增字段: `properties` (TEXT, JSON格式)
     - 存储组件属性配置，如：输入类型、超时设置、缓存开关等
   - 新增索引: `idx_module_category`

## 示例数据

### 基础组件 (category = 'basic')
1. **文本輸入** - 用户输入文本信息
2. **條件判斷** - 根据条件执行不同流程
3. **API調用** - 调用外部接口获取数据

### 高级组件 (category = 'advanced')
1. **AI模型** - 集成大语言模型能力
2. **數據分析** - 数据处理与分析模块
3. **插件集成** - 连接第三方服务

## 执行顺序

### 全新安装
```bash
# 方式1: 使用完整脚本（推荐）
mysql -u root -p < complete_schema.sql

# 方式2: 分步执行
mysql -u root -p < schema.sql
mysql -u root -p ai_drag_drop < update_schema.sql
```

### 已有数据库升级
```bash
# 执行更新脚本
mysql -u root -p ai_drag_drop < update_schema.sql
```

## 注意事项

1. **备份数据**: 执行更新脚本前请先备份现有数据
2. **外键约束**: 如果已有工作流数据，需要先创建项目并关联
3. **字段默认值**: `category` 字段默认为 'basic'
4. **JSON字段**: `properties` 字段存储JSON格式数据，需要确保格式正确

## 验证

执行脚本后，可以运行以下SQL验证：

```sql
-- 检查表是否存在
SHOW TABLES;

-- 检查projects表结构
DESC projects;

-- 检查workflows表是否有project_id字段
DESC workflows;

-- 检查ai_modules表是否有category和properties字段
DESC ai_modules;

-- 检查示例数据
SELECT id, name, category FROM ai_modules;
```

## 回滚（如果需要）

如果需要回滚更改，可以执行：

```sql
-- 删除外键约束
ALTER TABLE workflows DROP FOREIGN KEY fk_workflow_project;

-- 删除字段（谨慎操作）
ALTER TABLE workflows DROP COLUMN project_id;
ALTER TABLE ai_modules DROP COLUMN category;
ALTER TABLE ai_modules DROP COLUMN properties;

-- 删除表（谨慎操作）
DROP TABLE IF EXISTS projects;
```
