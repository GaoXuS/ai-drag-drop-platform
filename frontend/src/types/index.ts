// 模組類型定義
export interface AIModule {
  id: string
  name: string
  type: string
  description?: string
  icon?: string
  version: string
  config: ModuleConfig
  apiConfig: APIConfig
}

// 模組配置
export interface ModuleConfig {
  [key: string]: any
}

// API 配置
export interface APIConfig {
  url: string
  method: 'GET' | 'POST' | 'PUT' | 'DELETE'
  headers?: Record<string, string>
  timeout?: number
  auth?: {
    type: 'none' | 'basic' | 'bearer' | 'apiKey'
    credentials?: Record<string, string>
  }
}

// 模組節點（用於拖拉拽）
export interface ModuleNode {
  id: string
  moduleId: string
  position: { x: number; y: number }
  config: ModuleConfig
  connections: Connection[]
}

// 連接關係
export interface Connection {
  id: string
  fromNodeId: string
  fromPort: string
  toNodeId: string
  toPort: string
}

// 工作流
export interface Workflow {
  id: string
  name: string
  description?: string
  nodes: ModuleNode[]
  connections: Connection[]
  createdAt: string
  updatedAt: string
}

// 任務執行
export interface Task {
  id: string
  workflowId: string
  status: 'pending' | 'running' | 'completed' | 'failed'
  result?: any
  error?: string
  startTime?: string
  endTime?: string
  logs: TaskLog[]
}

// 任務日誌
export interface TaskLog {
  id: string
  taskId: string
  moduleId: string
  level: 'info' | 'warn' | 'error'
  message: string
  timestamp: string
  data?: any
}

