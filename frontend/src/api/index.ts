import axios from 'axios'
import type {
  AIModule,
  Workflow,
  Task,
  TaskLog,
  ModuleNode,
  Connection,
} from '@/types'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 可以在此添加认证 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// 模块 API
export const moduleApi = {
  // 获取所有模块
  getAllModules: (): Promise<AIModule[]> => api.get('/modules'),
  
  // 获取模块详情
  getModule: (id: string): Promise<AIModule> => api.get(`/modules/${id}`),
  
  // 创建模块
  createModule: (module: Partial<AIModule>): Promise<AIModule> =>
    api.post('/modules', module),
  
  // 更新模块
  updateModule: (id: string, module: Partial<AIModule>): Promise<AIModule> =>
    api.put(`/modules/${id}`, module),
  
  // 删除模块
  deleteModule: (id: string): Promise<void> => api.delete(`/modules/${id}`),
}

// 工作流 API
export const workflowApi = {
  // 获取所有工作流
  getAllWorkflows: (): Promise<Workflow[]> => api.get('/workflows'),
  
  // 获取工作流详情
  getWorkflow: (id: string): Promise<Workflow> => api.get(`/workflows/${id}`),
  
  // 创建工作流
  createWorkflow: (workflow: Partial<Workflow>): Promise<Workflow> =>
    api.post('/workflows', workflow),
  
  // 更新工作流
  updateWorkflow: (id: string, workflow: Partial<Workflow>): Promise<Workflow> =>
    api.put(`/workflows/${id}`, workflow),
  
  // 删除工作流
  deleteWorkflow: (id: string): Promise<void> => api.delete(`/workflows/${id}`),
  
  // 执行工作流
  executeWorkflow: (id: string, input?: any): Promise<Task> =>
    api.post(`/workflows/${id}/execute`, input),
}

// 任务 API
export const taskApi = {
  // 获取任务详情
  getTask: (id: string): Promise<Task> => api.get(`/tasks/${id}`),
  
  // 获取任务列表
  getTasks: (workflowId?: string): Promise<Task[]> =>
    api.get('/tasks', { params: { workflowId } }),
  
  // 获取任务日志
  getTaskLogs: (taskId: string): Promise<TaskLog[]> =>
    api.get(`/tasks/${taskId}/logs`),
  
  // 取消任务
  cancelTask: (id: string): Promise<void> => api.post(`/tasks/${id}/cancel`),
}

export default api

