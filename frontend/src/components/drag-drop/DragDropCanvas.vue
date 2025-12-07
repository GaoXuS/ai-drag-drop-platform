<template>
  <div 
    class="drag-drop-canvas" 
    ref="canvasRef" 
    @contextmenu.prevent
    @dragover.prevent="handleDragOver"
    @dragleave="handleDragLeave"
    @drop.prevent="handleDrop"
  >
    <svg class="connections-layer">
      <!-- 已存在的连接 -->
      <path
        v-for="conn in connections"
        :key="conn.id"
        :d="getConnectionPath(conn)"
        class="connection-path"
        stroke="#409eff"
        stroke-width="2"
        fill="none"
        marker-end="url(#arrowhead)"
        @contextmenu.stop="deleteConnection(conn.id)"
      />
      <!-- 连接预览线 -->
      <path
        v-if="previewPath"
        :d="previewPath"
        class="connection-preview"
        stroke="#67c23a"
        stroke-width="2"
        stroke-dasharray="5,5"
        fill="none"
        marker-end="url(#arrowhead-preview)"
      />
      <defs>
        <marker
          id="arrowhead"
          markerWidth="10"
          markerHeight="10"
          refX="9"
          refY="3"
          orient="auto"
        >
          <polygon points="0 0, 10 3, 0 6" fill="#409eff" />
        </marker>
        <marker
          id="arrowhead-preview"
          markerWidth="10"
          markerHeight="10"
          refX="9"
          refY="3"
          orient="auto"
        >
          <polygon points="0 0, 10 3, 0 6" fill="#67c23a" />
        </marker>
      </defs>
    </svg>
    <div
      v-for="node in nodes"
      :key="node.id"
      class="module-node"
      :class="{ 'node-dragging': draggingNode?.id === node.id }"
      :style="{
        left: node.position.x + 'px',
        top: node.position.y + 'px',
      }"
      @mousedown="startDrag(node, $event)"
      @click.stop="handleNodeClick(node)"
    >
      <div class="node-header">
        <div>
          <h3>{{ getModuleName(node.moduleId) }}</h3>
          <p>组件ID: {{ node.id }}</p>
        </div>
        <div class="component-actions">
          <div class="action-btn" @click.stop="handleNodeClick(node)">✏️</div>
          <div class="action-btn" @click.stop="deleteNode(node.id)">🗑️</div>
        </div>
      </div>
      <div class="node-body">
        <p>{{ getModuleDescription(node.moduleId) }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue'
import { ArrowLeft, ArrowRight, Close } from '@element-plus/icons-vue'
import { useModuleStore } from '@/stores/moduleStore'
import { ElMessageBox } from 'element-plus'
import type { ModuleNode, Connection } from '@/types'

interface Props {
  nodes: ModuleNode[]
  connections: Connection[]
}

interface Emits {
  (e: 'update:nodes', nodes: ModuleNode[]): void
  (e: 'update:connections', connections: Connection[]): void
  (e: 'node-click', node: ModuleNode): void
  (e: 'drop-module', moduleId: string, position: { x: number; y: number }): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const canvasRef = ref<HTMLElement>()
const moduleStore = useModuleStore()

let draggingNode: ModuleNode | null = null
let dragOffset = { x: 0, y: 0 }
let connectingFrom: { node: ModuleNode; port: string } | null = null
let previewEnd = ref<{ x: number; y: number } | null>(null)

const previewPath = computed(() => {
  if (!connectingFrom || !previewEnd.value) return null

  const fromNode = connectingFrom.node
  const isOutput = connectingFrom.port === 'output'
  const x1 = isOutput ? fromNode.position.x + 150 : fromNode.position.x
  const y1 = fromNode.position.y + 40
  const x2 = previewEnd.value.x
  const y2 = previewEnd.value.y

  const midX = (x1 + x2) / 2
  return `M ${x1} ${y1} C ${midX} ${y1}, ${midX} ${y2}, ${x2} ${y2}`
})

const getModuleName = (moduleId: string) => {
  const module = moduleStore.getModuleById(moduleId)
  return module?.name || moduleId
}

const getModuleDescription = (moduleId: string) => {
  const module = moduleStore.getModuleById(moduleId)
  return module?.description || '新添加的组件，请进行配置'
}

const startDrag = (node: ModuleNode, event: MouseEvent) => {
  // 如果正在连接，不启动拖拽
  if (connectingFrom) return

  draggingNode = node
  const rect = canvasRef.value?.getBoundingClientRect()
  if (rect) {
    dragOffset.x = event.clientX - rect.left - node.position.x
    dragOffset.y = event.clientY - rect.top - node.position.y
  }
  document.addEventListener('mousemove', handleDrag)
  document.addEventListener('mouseup', stopDrag)
}

const handleDrag = (event: MouseEvent) => {
  if (!draggingNode || !canvasRef.value) return

  const rect = canvasRef.value.getBoundingClientRect()
  const newX = event.clientX - rect.left - dragOffset.x
  const newY = event.clientY - rect.top - dragOffset.y

  draggingNode.position.x = Math.max(0, newX)
  draggingNode.position.y = Math.max(0, newY)

  emit('update:nodes', [...props.nodes])
}

const stopDrag = () => {
  draggingNode = null
  document.removeEventListener('mousemove', handleDrag)
  document.removeEventListener('mouseup', stopDrag)
}

const startConnection = (node: ModuleNode, port: string, event: MouseEvent) => {
  event.stopPropagation()
  connectingFrom = { node, port }
  const rect = canvasRef.value?.getBoundingClientRect()
  if (rect) {
    const isOutput = port === 'output'
    const x = isOutput ? node.position.x + 150 : node.position.x
    const y = node.position.y + 40
    previewEnd.value = {
      x: event.clientX - rect.left,
      y: event.clientY - rect.top,
    }
  }
  document.addEventListener('mousemove', handleConnection)
  document.addEventListener('mouseup', finishConnection)
}

const handleConnection = (event: MouseEvent) => {
  if (!connectingFrom || !canvasRef.value) return

  const rect = canvasRef.value.getBoundingClientRect()
  previewEnd.value = {
    x: event.clientX - rect.left,
    y: event.clientY - rect.top,
  }
}

const endConnection = (targetNode: ModuleNode, targetPort: string, event: MouseEvent) => {
  event.stopPropagation()
  
  if (!connectingFrom) return

  const { node: fromNode, port: fromPort } = connectingFrom

  // 不能连接到同一个节点
  if (fromNode.id === targetNode.id) {
    cancelConnection()
    return
  }

  // 只能从输出端口连接到输入端口
  if (fromPort === 'output' && targetPort === 'input') {
    // 检查是否已存在连接
    const existingConnection = props.connections.find(
      (conn) =>
        conn.fromNodeId === fromNode.id &&
        conn.toNodeId === targetNode.id &&
        conn.fromPort === fromPort &&
        conn.toPort === targetPort
    )

    if (!existingConnection) {
      const newConnection: Connection = {
        id: `conn-${Date.now()}`,
        fromNodeId: fromNode.id,
        fromPort: fromPort,
        toNodeId: targetNode.id,
        toPort: targetPort,
      }
      emit('update:connections', [...props.connections, newConnection])
    }
  }

  cancelConnection()
}

const finishConnection = (event: MouseEvent) => {
  // 如果鼠标在画布上释放，取消连接
  if (connectingFrom) {
    cancelConnection()
  }
}

const cancelConnection = () => {
  connectingFrom = null
  previewEnd.value = null
  document.removeEventListener('mousemove', handleConnection)
  document.removeEventListener('mouseup', finishConnection)
}

const deleteConnection = async (connectionId: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这条连接吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    emit(
      'update:connections',
      props.connections.filter((conn) => conn.id !== connectionId)
    )
  } catch {
    // 用户取消
  }
}

const deleteNode = async (nodeId: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这个节点吗？删除节点会同时删除相关连接。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    // 删除节点
    emit('update:nodes', props.nodes.filter((node) => node.id !== nodeId))
    
    // 删除相关连接
    emit(
      'update:connections',
      props.connections.filter(
        (conn) => conn.fromNodeId !== nodeId && conn.toNodeId !== nodeId
      )
    )
  } catch {
    // 用户取消
  }
}

const getConnectionPath = (conn: Connection): string => {
  const fromNode = props.nodes.find((n) => n.id === conn.fromNodeId)
  const toNode = props.nodes.find((n) => n.id === conn.toNodeId)

  if (!fromNode || !toNode) return ''

  const x1 = fromNode.position.x + 150 // 輸出端口位置
  const y1 = fromNode.position.y + 40
  const x2 = toNode.position.x // 輸入端口位置
  const y2 = toNode.position.y + 40

  const midX = (x1 + x2) / 2
  return `M ${x1} ${y1} C ${midX} ${y1}, ${midX} ${y2}, ${x2} ${y2}`
}

const handleNodeClick = (node: ModuleNode) => {
  emit('node-click', node)
}

const handleDragOver = (event: DragEvent) => {
  event.preventDefault()
  if (canvasRef.value) {
    canvasRef.value.style.borderColor = '#1890ff'
    canvasRef.value.style.backgroundColor = '#f0f7ff'
  }
}

const handleDragLeave = () => {
  if (canvasRef.value) {
    canvasRef.value.style.borderColor = '#e8eaed'
    canvasRef.value.style.backgroundColor = '#f5f7fa'
  }
}

const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  if (canvasRef.value) {
    canvasRef.value.style.borderColor = '#e8eaed'
    canvasRef.value.style.backgroundColor = '#f5f7fa'
  }
  
  const moduleId = event.dataTransfer?.getData('moduleId')
  if (moduleId && canvasRef.value) {
    const rect = canvasRef.value.getBoundingClientRect()
    const x = event.clientX - rect.left
    const y = event.clientY - rect.top
    emit('drop-module', moduleId, { x, y })
  }
}

onUnmounted(() => {
  document.removeEventListener('mousemove', handleDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('mousemove', handleConnection)
  document.removeEventListener('mouseup', finishConnection)
})
</script>

<style scoped>
.drag-drop-canvas {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 600px;
  background-color: #f5f7fa;
  background-image: 
    linear-gradient(rgba(0, 0, 0, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  overflow: auto;
  border: 2px dashed #e8eaed;
  border-radius: 8px;
  transition: all 0.3s;
}

.connections-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.connection-path {
  pointer-events: stroke;
  cursor: pointer;
  transition: stroke-width 0.2s;
}

.connection-path:hover {
  stroke-width: 3;
}

.connection-preview {
  pointer-events: none;
}

.node-dragging {
  opacity: 0.8;
  z-index: 100;
}

.port-active {
  background-color: #67c23a !important;
  transform: scale(1.2);
}

.node-delete {
  opacity: 0;
  transition: opacity 0.2s;
}

.module-node:hover .node-delete {
  opacity: 1;
}

.module-node {
  position: absolute;
  min-width: 200px;
  background: white;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: move;
  z-index: 10;
  user-select: none;
  transition: all 0.3s;
}

.module-node:hover {
  border-color: #1890ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.node-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
  background: transparent;
  color: #1f1f1f;
  border-radius: 0;
}

.node-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.node-header p {
  font-size: 12px;
  color: #666;
  margin: 4px 0 0 0;
}

.node-body {
  padding: 0 16px 16px 16px;
}

.node-body p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.component-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #e6f7ff;
  color: #1890ff;
}


.module-node:hover {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}
</style>

