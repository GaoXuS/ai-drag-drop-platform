# 安裝與配置指南

## 環境準備

### 1. 安裝 Java 21

**macOS (使用 Homebrew)**
```bash
brew install openjdk@21
```

**Linux (Ubuntu/Debian)**
```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

**驗證安裝**
```bash
java -version
```

### 2. 安裝 Maven 3.x

**macOS (使用 Homebrew)**
```bash
brew install maven
```

**Linux (Ubuntu/Debian)**
```bash
sudo apt install maven
```

**驗證安裝**
```bash
mvn -version
```

### 3. 安裝 Node.js 18+

**macOS (使用 Homebrew)**
```bash
brew install node@18
```

**Linux (使用 nvm)**
```bash
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 18
nvm use 18
```

**驗證安裝**
```bash
node -v
npm -v
```

### 4. 安裝 MySQL/PostgreSQL

**MySQL (macOS)**
```bash
brew install mysql
brew services start mysql
```

**PostgreSQL (macOS)**
```bash
brew install postgresql@14
brew services start postgresql@14
```

### 5. 安裝 Redis

**macOS**
```bash
brew install redis
brew services start redis
```

**Linux (Ubuntu/Debian)**
```bash
sudo apt install redis-server
sudo systemctl start redis
```

**驗證安裝**
```bash
redis-cli ping
# 應該返回 PONG
```

## 數據庫配置

### MySQL 配置

1. 登錄 MySQL
```bash
mysql -u root -p
```

2. 創建數據庫
```sql
CREATE DATABASE ai_drag_drop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 創建用戶（可選）
```sql
CREATE USER 'aidragdrop'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON ai_drag_drop.* TO 'aidragdrop'@'localhost';
FLUSH PRIVILEGES;
```

### PostgreSQL 配置

1. 登錄 PostgreSQL
```bash
psql -U postgres
```

2. 創建數據庫
```sql
CREATE DATABASE ai_drag_drop;
```

## 項目配置

### 後端配置

1. 編輯 `backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_drag_drop?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  
  data:
    redis:
      host: localhost
      port: 6379
      password: 
```

### 前端配置

前端配置已通過 Vite 代理設置，無需額外配置。

## 啟動項目

### 1. 啟動後端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

後端服務將在 `http://localhost:8080` 啟動

### 2. 啟動前端

```bash
cd frontend
npm install
npm run dev
```

前端服務將在 `http://localhost:3000` 啟動

## 驗證安裝

1. 訪問前端：`http://localhost:3000`
2. 檢查後端 API：`http://localhost:8080/api/modules`
3. 檢查數據庫連接：查看後端日誌
4. 檢查 Redis 連接：查看後端日誌

## 常見問題

### 問題 1: Java 版本不匹配

**錯誤**: `Unsupported class file major version`

**解決**: 確保使用 Java 21
```bash
java -version
# 應該顯示 java version "21.x.x"
```

### 問題 2: 數據庫連接失敗

**錯誤**: `Communications link failure`

**解決**: 
1. 檢查數據庫服務是否運行
2. 檢查 `application.yml` 中的數據庫配置
3. 檢查防火牆設置

### 問題 3: Redis 連接失敗

**錯誤**: `Unable to connect to Redis`

**解決**:
1. 檢查 Redis 服務是否運行：`redis-cli ping`
2. 檢查 `application.yml` 中的 Redis 配置
3. 檢查 Redis 是否需要密碼

### 問題 4: 前端依賴安裝失敗

**錯誤**: `npm ERR!`

**解決**:
1. 清除緩存：`npm cache clean --force`
2. 刪除 `node_modules` 和 `package-lock.json`
3. 重新安裝：`npm install`

## 開發工具推薦

- **IDE**: IntelliJ IDEA (後端) / VS Code (前端)
- **數據庫管理**: DBeaver / MySQL Workbench
- **API 測試**: Postman / Insomnia
- **Redis 管理**: RedisInsight

