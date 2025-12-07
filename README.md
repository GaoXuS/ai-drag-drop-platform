# 模块化 AI 拖拽平台

## 项目简介

本项目是一个模块化 AI 拖拽平台，支持用户通过可视化拖拽方式组合 AI 模块，进行 AI 功能调用。平台提供高度可扩展和可复用的模块化设计，允许其他项目直接集成该功能。

## 技術棧

### 前端
- Vue 3
- Vite
- TypeScript
- 拖拉拽庫（可選：vue-draggable-plus 或 SortableJS）

### 後端
- Spring Boot 3.5.5
- Java 21
- Maven
- MySQL/PostgreSQL
- Redis

## 項目結構

```
ai-drag-drop-platform/
├── frontend/          # 前端项目
├── backend/           # 后端项目
├── docs/              # 项目文档
└── README.md          # 项目说明
```

## 环境要求

- Java 21
- Maven 3.x
- Node.js 18+
- MySQL/PostgreSQL
- Redis

## 快速開始

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

### 后端启动

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

## 功能特性

- ✅ 可视化拖拽界面
- ✅ 模块化 AI 调用
- ✅ 自定义 API 配置
- ✅ 任务执行日志
- ✅ 模块注册与管理
- ✅ 跨项目复用支持

## 开发计划

详见 `docs/development-plan.md`

## 授权

MIT License

