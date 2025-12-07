<template>
  <div class="workflow-view">
    <el-container>
      <el-header>
        <div class="header-content">
          <h2>工作流設計器</h2>
          <div class="header-actions">
            <el-button @click="saveWorkflow" :loading="saving">保存</el-button>
            <el-button type="primary" @click="executeWorkflow" :loading="executing">執行</el-button>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="250px">
          <ModuleList @add-module="addModule" />
        </el-aside>
        <el-main>
          <DragDropCanvas
            :nodes="nodes"
            :connections="connections"
            @update:nodes="updateNodes"
            @update:connections="updateConnections"
            @node-click="handleNodeClick"
          />
        </el-main>
      </el-container>
    </el-container>
    <ModuleEditor
      v-if="selectedNode"
      v-model="showEditor"
      :node="selectedNode"
      @update="updateNodeConfig"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkflowStore } from '@/stores/workflowStore'
import { useModuleStore } from '@/stores/moduleStore'
import { ElMessage } from 'element-plus'
import type { ModuleNode, Connection } from '@/types'
import DragDropCanvas from '@/components/drag-drop/DragDropCanvas.vue'
import ModuleList from '@/components/module-list/ModuleList.vue'
import ModuleEditor from '@/components/module-editor/ModuleEditor.vue'

const route = useRoute()
const router = useRouter()
const workflowStore = useWorkflowStore()
const moduleStore = useModuleStore()

const nodes = ref<ModuleNode[]>([])
const connections = ref<Connection[]>([])
const selectedNode = ref<ModuleNode | null>(null)
const showEditor = ref(false)
const saving = ref(false)
const executing = ref(false)

const workflowId = computed(() => route.params.id as string | undefined)

onMounted(async () => {
  await moduleStore.fetchModules()
  if (workflowId.value) {
    await loadWorkflow(workflowId.value)
  }
})

const loadWorkflow = async (id: string) => {
  try {
    const workflow = await workflowStore.fetchWorkflow(id)
    nodes.value = workflow.nodes || []
    connections.value = workflow.connections || []
  } catch (error) {
    ElMessage.error('加載工作流失敗')
  }
}

const addModule = (moduleId: string) => {
  const module = moduleStore.getModuleById(moduleId)
  if (!module) return

  const newNode: ModuleNode = {
    id: `node-${Date.now()}`,
    moduleId: module.id,
    position: { x: 100, y: 100 },
    config: { ...module.config },
    connections: [],
  }
  nodes.value.push(newNode)
}

const updateNodes = (newNodes: ModuleNode[]) => {
  nodes.value = newNodes
}

const updateConnections = (newConnections: Connection[]) => {
  connections.value = newConnections
}

const handleNodeClick = (node: ModuleNode) => {
  selectedNode.value = node
  showEditor.value = true
}

const updateNodeConfig = (nodeId: string, config: any) => {
  const node = nodes.value.find((n) => n.id === nodeId)
  if (node) {
    node.config = config
  }
  showEditor.value = false
  selectedNode.value = null
}

const saveWorkflow = async () => {
  saving.value = true
  try {
    const workflowData = {
      name: `工作流-${Date.now()}`,
      nodes: nodes.value,
      connections: connections.value,
    }

    if (workflowId.value) {
      await workflowStore.updateWorkflow(workflowId.value, workflowData)
      ElMessage.success('工作流已保存')
    } else {
      const workflow = await workflowStore.createWorkflow(workflowData)
      router.replace(`/workflow/${workflow.id}`)
      ElMessage.success('工作流已創建')
    }
  } catch (error) {
    ElMessage.error('保存工作流失敗')
  } finally {
    saving.value = false
  }
}

const executeWorkflow = async () => {
  if (!workflowId.value) {
    ElMessage.warning('請先保存工作流')
    return
  }

  executing.value = true
  try {
    const task = await workflowStore.executeWorkflow(workflowId.value)
    ElMessage.success('任務已提交')
    router.push(`/tasks?taskId=${task.id}`)
  } catch (error) {
    ElMessage.error('執行工作流失敗')
  } finally {
    executing.value = false
  }
}
</script>

<style scoped>
.workflow-view {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.el-header {
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-content h2 {
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.el-aside {
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
}

.el-main {
  padding: 0;
  background-color: #f5f7fa;
}
</style>

