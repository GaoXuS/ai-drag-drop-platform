import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/ProjectsView.vue'),
  },
  {
    path: '/projects',
    name: 'Projects',
    component: () => import('@/views/ProjectsView.vue'),
  },
  {
    path: '/project/:projectId',
    name: 'Project',
    component: () => import('@/views/ProjectView.vue'),
  },
  {
    path: '/workflow/:id?',
    name: 'Workflow',
    component: () => import('@/views/WorkflowView.vue'),
  },
  {
    path: '/modules',
    name: 'Modules',
    component: () => import('@/views/ModulesView.vue'),
  },
  {
    path: '/tasks',
    name: 'Tasks',
    component: () => import('@/views/TasksView.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router

