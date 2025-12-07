<template>
  <div class="project-view">
    <!-- 顶部栏 -->
    <header class="platform-header">
      <div class="logo">
        <div class="logo-icon">AI</div>
        <span>智能体搭建平台</span>
        <span class="project-name">- {{ projectStore.currentProject?.name || '项目工作流' }}</span>
      </div>
      <div class="header-actions">
        <el-button @click="saveWorkflow" :loading="saving">保存草稿</el-button>
        <el-button type="primary" @click="executeWorkflow" :loading="executing" :disabled="!workflowId">
          发布智能体
        </el-button>
        <el-button text @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </header>

    <!-- 主内容区 - 三栏布局 -->
    <div class="main-container">
      <!-- 左侧组件库 -->
      <div class="components-panel-wrapper">
        <ModuleList @add-module="addModule" />
      </div>

      <!-- 中间画布 -->
      <div class="canvas-container">
        <div class="canvas" id="dropCanvas" ref="canvasRef">
          <h2 class="canvas-title" v-if="nodes.length === 0">
            拖拽组件到此处构建智能体工作流
          </h2>
          <DragDropCanvas
            :nodes="nodes"
            :connections="connections"
            @update:nodes="updateNodes"
            @update:connections="updateConnections"
            @node-click="handleNodeClick"
            @drop-module="handleDropModule"
          />
        </div>
      </div>

      <!-- 右侧属性面板 -->
      <div class="properties-panel-wrapper">
        <PropertiesPanel
          :selected-node="selectedNode"
          @update="updateNodeConfig"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkflowStore } from '@/stores/workflowStore'
import { useModuleStore } from '@/stores/moduleStore'
import { useProjectStore } from '@/stores/projectStore'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import type { ModuleNode, Connection } from '@/types'
import DragDropCanvas from '@/components/drag-drop/DragDropCanvas.vue'
import ModuleList from '@/components/module-list/ModuleList.vue'
import PropertiesPanel from '@/components/properties-panel/PropertiesPanel.vue'

const route = useRoute()
const router = useRouter()
const workflowStore = useWorkflowStore()
const moduleStore = useModuleStore()
const projectStore = useProjectStore()

const nodes = ref<ModuleNode[]>([])
const connections = ref<Connection[]>([])
const selectedNode = ref<ModuleNode | null>(null)
const saving = ref(false)
const executing = ref(false)
const canvasRef = ref<HTMLElement>()

const projectId = computed(() => route.params.projectId as string)
const workflowId = ref<string | null>(null)

onMounted(async () => {
  await Promise.all([
    moduleStore.fetchModules(),
    projectStore.fetchProject(projectId.value),
  ])
  
  // 加载或创建工作流
  await loadOrCreateWorkflow()
})

const loadOrCreateWorkflow = async () => {
  try {
    // 尝试获取项目的工作流
    await workflowStore.fetchWorkflows(projectId.value)
    const projectWorkflow = workflowStore.workflows.find(w => w.projectId === projectId.value)
    
    if (projectWorkflow) {
      workflowId.value = projectWorkflow.id
      nodes.value = projectWorkflow.nodes || []
      connections.value = projectWorkflow.connections || []
    } else {
      // 如果没有工作流，创建一个新的
      const newWorkflow = await workflowStore.createWorkflow({
        name: `${projectStore.currentProject?.name || '项目'}工作流`,
        projectId: projectId.value,
        nodes: [],
        connections: [],
      })
      workflowId.value = newWorkflow.id
    }
  } catch (error: any) {
    ElMessage.error('加载工作流失败: ' + (error.message || ''))
  }
}

const setupCanvasDrop = () => {
  // 拖拽事件现在由DragDropCanvas组件内部处理
  // 这里保留用于兼容性，但主要逻辑在handleDropModule中
}

const handleDropModule = (moduleId: string, position: { x: number; y: number }) => {
  addModule(moduleId, position)
}

const addModule = (moduleId: string, position?: { x: number; y: number }) => {
  const module = moduleStore.getModuleById(moduleId)
  if (!module) {
    ElMessage.warning('模块不存在')
    return
  }

  const newNode: ModuleNode = {
    id: `node-${Date.now()}`,
    moduleId: module.id,
    position: position || { x: Math.random() * 300 + 100, y: Math.random() * 300 + 100 },
    config: { 
      ...module.config,
      properties: module.properties || {},
    },
    connections: [],
  }
  nodes.value.push(newNode)
  ElMessage.success(`已添加组件: ${module.name}`)
}

const updateNodes = (newNodes: ModuleNode[]) => {
  nodes.value = newNodes
}

const updateConnections = (newConnections: Connection[]) => {
  connections.value = newConnections
}

const handleNodeClick = (node: ModuleNode) => {
  selectedNode.value = node
}

const updateNodeConfig = (nodeId: string, config: any) => {
  const node = nodes.value.find((n) => n.id === nodeId)
  if (node) {
    node.config = config
  }
  selectedNode.value = null
}

const saveWorkflow = async () => {
  if (!workflowId.value) {
    ElMessage.warning('工作流未初始化')
    return
  }

  saving.value = true
  try {
    await workflowStore.updateWorkflow(workflowId.value, {
      name: `${projectStore.currentProject?.name || '项目'}工作流`,
      projectId: projectId.value,
      nodes: nodes.value,
      connections: connections.value,
    })
    ElMessage.success('工作流已保存')
  } catch (error: any) {
    ElMessage.error('保存工作流失败: ' + (error.message || ''))
  } finally {
    saving.value = false
  }
}

const executeWorkflow = async () => {
  if (!workflowId.value) {
    ElMessage.warning('请先保存工作流')
    return
  }

  if (nodes.value.length === 0) {
    ElMessage.warning('请至少添加一个模块节点')
    return
  }

  executing.value = true
  try {
    const task = await workflowStore.executeWorkflow(workflowId.value)
    ElMessage.success('任务已提交')
    router.push(`/tasks?taskId=${task.id}`)
  } catch (error: any) {
    ElMessage.error('执行工作流失败: ' + (error.message || ''))
  } finally {
    executing.value = false
  }
}

const goBack = () => {
  router.push('/projects')
}
</script>

<style scoped>
.project-view {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

/* 顶部栏 */
.platform-header {
  background: white;
  height: 60px;
  border-bottom: 1px solid #e8eaed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #1890ff, #52c41a);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
}

.project-name {
  color: #666;
  font-weight: normal;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 主内容区 - 三栏布局 */
.main-container {
  display: flex;
  flex: 1;
  height: calc(100vh - 60px);
  overflow: hidden;
}

.components-panel-wrapper {
  width: 280px;
  flex-shrink: 0;
  overflow: hidden;
}

.canvas-container {
  flex: 1;
  padding: 24px;
  overflow: auto;
  background: #fafafa;
}

.canvas {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  min-height: 600px;
  padding: 32px;
  border: 2px dashed #e8eaed;
  position: relative;
  transition: all 0.3s;
}

.canvas-title {
  text-align: center;
  color: #999;
  margin: 32px 0;
  font-size: 18px;
  font-weight: normal;
}

.properties-panel-wrapper {
  width: 320px;
  flex-shrink: 0;
  overflow: hidden;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .components-panel-wrapper {
    width: 240px;
  }
  .properties-panel-wrapper {
    width: 280px;
  }
}
</style>
