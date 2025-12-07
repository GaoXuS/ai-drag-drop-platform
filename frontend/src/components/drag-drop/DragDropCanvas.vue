<template>
  <div class="drag-drop-canvas" ref="canvasRef">
    <svg class="connections-layer">
      <path
        v-for="conn in connections"
        :key="conn.id"
        :d="getConnectionPath(conn)"
        class="connection-path"
        stroke="#409eff"
        stroke-width="2"
        fill="none"
        marker-end="url(#arrowhead)"
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
      </defs>
    </svg>
    <div
      v-for="node in nodes"
      :key="node.id"
      class="module-node"
      :style="{
        left: node.position.x + 'px',
        top: node.position.y + 'px',
      }"
      @mousedown="startDrag(node, $event)"
      @click.stop="handleNodeClick(node)"
    >
      <div class="node-header">
        <span class="node-title">{{ getModuleName(node.moduleId) }}</span>
      </div>
      <div class="node-body">
        <div class="node-port input-port" @mousedown.stop="startConnection(node, 'input', $event)">
          <el-icon><ArrowLeft /></el-icon>
        </div>
        <div class="node-content">配置模組</div>
        <div class="node-port output-port" @mousedown.stop="startConnection(node, 'output', $event)">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { useModuleStore } from '@/stores/moduleStore'
import type { ModuleNode, Connection } from '@/types'

interface Props {
  nodes: ModuleNode[]
  connections: Connection[]
}

interface Emits {
  (e: 'update:nodes', nodes: ModuleNode[]): void
  (e: 'update:connections', connections: Connection[]): void
  (e: 'node-click', node: ModuleNode): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const canvasRef = ref<HTMLElement>()
const moduleStore = useModuleStore()

let draggingNode: ModuleNode | null = null
let dragOffset = { x: 0, y: 0 }
let connectingFrom: { node: ModuleNode; port: string } | null = null

const getModuleName = (moduleId: string) => {
  const module = moduleStore.getModuleById(moduleId)
  return module?.name || moduleId
}

const startDrag = (node: ModuleNode, event: MouseEvent) => {
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
  document.addEventListener('mousemove', handleConnection)
  document.addEventListener('mouseup', finishConnection)
}

const handleConnection = (event: MouseEvent) => {
  // 可以在此處繪製連接線預覽
}

const finishConnection = (event: MouseEvent) => {
  if (!connectingFrom) return

  // 簡化版：需要實現節點碰撞檢測
  // 這裡只是示例，實際需要檢測鼠標位置是否在另一個節點的端口上

  document.removeEventListener('mousemove', handleConnection)
  document.removeEventListener('mouseup', finishConnection)
  connectingFrom = null
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
  background-color: #f5f7fa;
  background-image: 
    linear-gradient(rgba(0, 0, 0, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  overflow: hidden;
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
}

.module-node {
  position: absolute;
  min-width: 150px;
  background: white;
  border: 2px solid #409eff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: move;
  z-index: 10;
  user-select: none;
}

.node-header {
  padding: 8px 12px;
  background-color: #409eff;
  color: white;
  border-radius: 6px 6px 0 0;
  font-weight: bold;
  font-size: 14px;
}

.node-body {
  display: flex;
  align-items: center;
  padding: 12px;
  gap: 8px;
}

.node-port {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: crosshair;
  flex-shrink: 0;
}

.node-content {
  flex: 1;
  text-align: center;
  color: #666;
  font-size: 12px;
}

.module-node:hover {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}
</style>

