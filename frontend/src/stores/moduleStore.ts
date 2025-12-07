import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { AIModule } from '@/types'
import { moduleApi } from '@/api'

export const useModuleStore = defineStore('module', () => {
  const modules = ref<AIModule[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 获取所有模块
  const fetchModules = async () => {
    loading.value = true
    error.value = null
    try {
      modules.value = await moduleApi.getAllModules()
    } catch (err: any) {
      error.value = err.message || '获取模块失败'
      console.error('Fetch modules error:', err)
    } finally {
      loading.value = false
    }
  }

  // 获取模块详情
  const fetchModule = async (id: string) => {
    loading.value = true
    error.value = null
    try {
      const module = await moduleApi.getModule(id)
      const index = modules.value.findIndex((m) => m.id === id)
      if (index >= 0) {
        modules.value[index] = module
      } else {
        modules.value.push(module)
      }
      return module
    } catch (err: any) {
      error.value = err.message || '获取模块详情失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 创建模块
  const createModule = async (module: Partial<AIModule>) => {
    loading.value = true
    error.value = null
    try {
      const newModule = await moduleApi.createModule(module)
      modules.value.push(newModule)
      return newModule
    } catch (err: any) {
      error.value = err.message || '创建模块失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 更新模組
  const updateModule = async (id: string, module: Partial<AIModule>) => {
    loading.value = true
    error.value = null
    try {
      const updatedModule = await moduleApi.updateModule(id, module)
      const index = modules.value.findIndex((m) => m.id === id)
      if (index >= 0) {
        modules.value[index] = updatedModule
      }
      return updatedModule
    } catch (err: any) {
      error.value = err.message || '更新模組失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 刪除模組
  const deleteModule = async (id: string) => {
    loading.value = true
    error.value = null
    try {
      await moduleApi.deleteModule(id)
      modules.value = modules.value.filter((m) => m.id !== id)
    } catch (err: any) {
      error.value = err.message || '刪除模組失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 根據 ID 獲取模組
  const getModuleById = (id: string) => {
    return modules.value.find((m) => m.id === id)
  }

  return {
    modules,
    loading,
    error,
    fetchModules,
    fetchModule,
    createModule,
    updateModule,
    deleteModule,
    getModuleById,
  }
})

