import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Project } from '@/types'
import { projectApi } from '@/api'

export const useProjectStore = defineStore('project', () => {
  const projects = ref<Project[]>([])
  const currentProject = ref<Project | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 獲取所有項目
  const fetchProjects = async () => {
    loading.value = true
    error.value = null
    try {
      projects.value = await projectApi.getAllProjects()
    } catch (err: any) {
      error.value = err.message || '獲取項目列表失敗'
      console.error('Fetch projects error:', err)
    } finally {
      loading.value = false
    }
  }

  // 獲取項目詳情
  const fetchProject = async (id: string) => {
    loading.value = true
    error.value = null
    try {
      const project = await projectApi.getProject(id)
      currentProject.value = project
      const index = projects.value.findIndex((p) => p.id === id)
      if (index >= 0) {
        projects.value[index] = project
      } else {
        projects.value.push(project)
      }
      return project
    } catch (err: any) {
      error.value = err.message || '獲取項目詳情失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 創建項目
  const createProject = async (project: Partial<Project>) => {
    loading.value = true
    error.value = null
    try {
      const newProject = await projectApi.createProject(project)
      projects.value.push(newProject)
      return newProject
    } catch (err: any) {
      error.value = err.message || '創建項目失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 更新項目
  const updateProject = async (id: string, project: Partial<Project>) => {
    loading.value = true
    error.value = null
    try {
      const updatedProject = await projectApi.updateProject(id, project)
      const index = projects.value.findIndex((p) => p.id === id)
      if (index >= 0) {
        projects.value[index] = updatedProject
      }
      if (currentProject.value?.id === id) {
        currentProject.value = updatedProject
      }
      return updatedProject
    } catch (err: any) {
      error.value = err.message || '更新項目失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 刪除項目
  const deleteProject = async (id: string) => {
    loading.value = true
    error.value = null
    try {
      await projectApi.deleteProject(id)
      projects.value = projects.value.filter((p) => p.id !== id)
      if (currentProject.value?.id === id) {
        currentProject.value = null
      }
    } catch (err: any) {
      error.value = err.message || '刪除項目失敗'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    projects,
    currentProject,
    loading,
    error,
    fetchProjects,
    fetchProject,
    createProject,
    updateProject,
    deleteProject,
  }
})


