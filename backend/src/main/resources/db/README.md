# 数据库脚本说明

## 文件说明

### 1. schema.sql
完整的数据库结构脚本，包含：
- 创建数据库
- 创建所有表结构
- 创建索引和外键
- 插入示例数据（可选）

**使用场景**：手动创建完整的数据库结构

**执行方式**：
```bash
mysql -u root -p < schema.sql
```

或在 MySQL 客户端中：
```sql
source schema.sql;
```

### 2. init.sql
简化的初始化脚本，仅创建数据库

**使用场景**：如果使用 JPA 自动创建表（`ddl-auto: update`），只需要创建数据库即可

**执行方式**：
```bash
mysql -u root -p < init.sql
```

## 两种使用方式

### 方式一：手动创建表结构（推荐用于生产环境）

1. 执行 `schema.sql` 创建完整的数据库结构
2. 修改 `application.yml`，设置：
   ```yaml
   spring:
     jpa:
       hibernate:
         ddl-auto: validate  # 或 none
   ```

### 方式二：JPA 自动创建表（推荐用于开发环境）

1. 执行 `init.sql` 仅创建数据库
2. 保持 `application.yml` 中的配置：
   ```yaml
   spring:
     jpa:
       hibernate:
         ddl-auto: update
   ```
3. 启动应用后，JPA 会自动创建表结构

## 数据库配置

在 `application.yml` 中配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_drag_drop?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

## 表结构说明

### ai_modules - AI 模块表
存储所有可用的 AI 模块配置信息

### workflows - 工作流表
存储用户创建的工作流定义

### tasks - 任务表
存储工作流执行任务的状态和结果

### task_logs - 任务日志表
存储任务执行过程中的日志记录

## 注意事项

1. **字符集**：使用 `utf8mb4` 以支持完整的 Unicode 字符（包括 emoji）
2. **外键约束**：tasks 和 task_logs 表有外键约束，删除工作流或任务时会级联删除相关记录
3. **索引**：已为常用查询字段创建索引，提升查询性能
4. **UUID**：所有主键使用 UUID（36 字符），由应用层生成

