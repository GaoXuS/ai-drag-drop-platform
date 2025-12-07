import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Workflow, Task } from '@/types'
import { workflowApi, taskApi } from '@/api'

export const useWorkflowStore = defineStore('workflow', () => {
  const workflows = ref<Workflow[]>([])
  const currentWorkflow = ref<Workflow | null>(null)
  const tasks = ref<Task[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 獲取所有工作流
  const fetchWorkflows = async () => {
    loading.value = true
    error.value = null
    try {
      workflows.value = await workflowApi.getAllWorkflows()
    } catch (err: any) {
      error.value = err.message || '獲取工作流失敗'
      console.error('Fetch workflows error:', err)
    } finally {
      loading.value = false
    }
  }

  // 獲取工作流詳情
  const fetchWorkflow = async (id: string) => {
    loading.value = true
    error.value = null
    try {
      const workflow = await workflowApi.getWorkflow(id)
      currentWorkflow.value = workflow
      const index = workflows.value.findIndex((w) => w.id === id)
      if (index >= 0) {
        workflows.value[index] = workflow
      } else {
        workflows.value.push(workflow)
      }
      return workflow
    } catch (err: any) {
      error.value = err.message || '獲取工作流詳情失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 創建工作流
  const createWorkflow = async (workflow: Partial<Workflow>) => {
    loading.value = true
    error.value = null
    try {
      const newWorkflow = await workflowApi.createWorkflow(workflow)
      workflows.value.push(newWorkflow)
      return newWorkflow
    } catch (err: any) {
      error.value = err.message || '創建工作流失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 更新工作流
  const updateWorkflow = async (id: string, workflow: Partial<Workflow>) => {
    loading.value = true
    error.value = null
    try {
      const updatedWorkflow = await workflowApi.updateWorkflow(id, workflow)
      const index = workflows.value.findIndex((w) => w.id === id)
      if (index >= 0) {
        workflows.value[index] = updatedWorkflow
      }
      if (currentWorkflow.value?.id === id) {
        currentWorkflow.value = updatedWorkflow
      }
      return updatedWorkflow
    } catch (err: any) {
      error.value = err.message || '更新工作流失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 刪除工作流
  const deleteWorkflow = async (id: string) => {
    loading.value = true
    error.value = null
    try {
      await workflowApi.deleteWorkflow(id)
      workflows.value = workflows.value.filter((w) => w.id !== id)
      if (currentWorkflow.value?.id === id) {
        currentWorkflow.value = null
      }
    } catch (err: any) {
      error.value = err.message || '刪除工作流失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 執行工作流
  const executeWorkflow = async (id: string, input?: any) => {
    loading.value = true
    error.value = null
    try {
      const task = await workflowApi.executeWorkflow(id, input)
      tasks.value.unshift(task)
      return task
    } catch (err: any) {
      error.value = err.message || '執行工作流失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 獲取任務列表
  const fetchTasks = async (workflowId?: string) => {
    loading.value = true
    error.value = null
    try {
      tasks.value = await taskApi.getTasks(workflowId)
    } catch (err: any) {
      error.value = err.message || '獲取任務列表失敗'
      console.error('Fetch tasks error:', err)
    } finally {
      loading.value = false
    }
  }

  return {
    workflows,
    currentWorkflow,
    tasks,
    loading,
    error,
    fetchWorkflows,
    fetchWorkflow,
    createWorkflow,
    updateWorkflow,
    deleteWorkflow,
    executeWorkflow,
    fetchTasks,
  }
})

