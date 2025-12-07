<template>
  <div class="properties-panel">
    <div class="panel-title">
      <div class="component-icon">⚙️</div>
      <span>属性配置</span>
    </div>

    <div v-if="selectedNode" class="property-content">
      <div class="property-group">
        <h4 class="property-title">基本设置</h4>
        <div class="property-item">
          <label class="property-label">组件名称</label>
          <el-input
            v-model="nodeName"
            placeholder="请输入组件名称"
            @change="updateProperty('name', nodeName)"
          />
        </div>
        <div class="property-item">
          <label class="property-label">组件ID</label>
          <el-input
            :value="selectedNode.id"
            readonly
            disabled
          />
        </div>
      </div>

      <div class="property-group" v-if="moduleProperties">
        <h4 class="property-title">参数配置</h4>
        <div class="property-item" v-if="moduleProperties.inputType !== undefined">
          <label class="property-label">输入类型</label>
          <el-select
            v-model="inputType"
            placeholder="请选择输入类型"
            @change="updateProperty('inputType', inputType)"
            style="width: 100%"
          >
            <el-option label="文本" value="text" />
            <el-option label="语音" value="voice" />
            <el-option label="图像" value="image" />
          </el-select>
        </div>
        <div class="property-item" v-if="moduleProperties.timeout !== undefined">
          <label class="property-label">超时设置(秒)</label>
          <el-slider
            v-model="timeout"
            :min="1"
            :max="30"
            :step="1"
            show-input
            @change="updateProperty('timeout', timeout)"
          />
        </div>
      </div>

      <div class="property-group">
        <h4 class="property-title">高级选项</h4>
        <div class="property-item">
          <el-checkbox
            v-model="enableCache"
            @change="updateProperty('enableCache', enableCache)"
          >
            启用缓存
          </el-checkbox>
        </div>
        <div class="property-item">
          <el-checkbox
            v-model="enableLog"
            @change="updateProperty('enableLog', enableLog)"
          >
            启用日志记录
          </el-checkbox>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <el-empty description="请选择一个组件进行配置" :image-size="100" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useModuleStore } from '@/stores/moduleStore'
import type { ModuleNode } from '@/types'

interface Props {
  selectedNode: ModuleNode | null
}

interface Emits {
  (e: 'update', nodeId: string, config: any): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
const moduleStore = useModuleStore()

const nodeName = ref('')
const inputType = ref<'text' | 'voice' | 'image'>('text')
const timeout = ref(10)
const enableCache = ref(true)
const enableLog = ref(false)

const moduleProperties = computed(() => {
  if (!props.selectedNode) return null
  const module = moduleStore.getModuleById(props.selectedNode.moduleId)
  return module?.properties || {}
})

watch(
  () => props.selectedNode,
  (node) => {
    if (node) {
      const module = moduleStore.getModuleById(node.moduleId)
      nodeName.value = module?.name || ''
      const props = node.config?.properties || module?.properties || {}
      inputType.value = props.inputType || 'text'
      timeout.value = props.timeout || 10
      enableCache.value = props.enableCache !== false
      enableLog.value = props.enableLog === true
    }
  },
  { immediate: true }
)

const updateProperty = (key: string, value: any) => {
  if (!props.selectedNode) return
  
  const newConfig = {
    ...props.selectedNode.config,
    properties: {
      ...(props.selectedNode.config?.properties || {}),
      [key]: value,
    },
  }
  
  emit('update', props.selectedNode.id, newConfig)
}
</script>

<style scoped>
.properties-panel {
  width: 320px;
  height: 100%;
  background: white;
  border-left: 1px solid #e8eaed;
  padding: 24px;
  overflow-y: auto;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #1f1f1f;
  display: flex;
  align-items: center;
  gap: 8px;
}

.component-icon {
  width: 32px;
  height: 32px;
  background: #f0f7ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
  font-size: 18px;
}

.property-content {
  flex: 1;
}

.property-group {
  margin-bottom: 24px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
}

.property-title {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: #1f1f1f;
}

.property-item {
  margin-bottom: 16px;
}

.property-item:last-child {
  margin-bottom: 0;
}

.property-label {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
  display: block;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}
</style>

