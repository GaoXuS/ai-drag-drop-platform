<template>
  <div class="components-panel">
    <div class="panel-title">
      <div class="component-icon">📦</div>
      <span>组件库</span>
    </div>

    <div class="components-category" v-for="category in categories" :key="category.key">
      <h3 class="category-title">{{ category.label }}</h3>
      <div class="component-list">
        <div
          v-for="module in getModulesByCategory(category.key)"
          :key="module.id"
          class="component-item"
          draggable="true"
          @dragstart="handleDragStart(module, $event)"
          @click="handleAddModule(module.id)"
        >
          <div class="component-icon">{{ module.icon || '📦' }}</div>
          <div class="component-info">
            <h4>{{ module.name }}</h4>
            <p>{{ module.description || '暂无描述' }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useModuleStore } from '@/stores/moduleStore'
import type { AIModule } from '@/types'

interface Emits {
  (e: 'add-module', moduleId: string): void
}

const emit = defineEmits<Emits>()
const moduleStore = useModuleStore()

const categories = [
  { key: 'basic', label: '基础组件' },
  { key: 'advanced', label: '高级组件' },
]

const modules = computed(() => moduleStore.modules)

const getModulesByCategory = (category: string) => {
  return modules.value.filter(
    (m) => (m.category || 'basic') === category
  )
}

const handleDragStart = (module: AIModule, event: DragEvent) => {
  if (event.dataTransfer) {
    event.dataTransfer.setData('moduleId', module.id)
    event.dataTransfer.setData('moduleName', module.name)
  }
}

const handleAddModule = (moduleId: string) => {
  emit('add-module', moduleId)
}
</script>

<style scoped>
.components-panel {
  width: 280px;
  height: 100%;
  background: white;
  border-right: 1px solid #e8eaed;
  padding: 20px;
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

.components-category {
  margin-bottom: 24px;
}

.category-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
  font-weight: 500;
}

.component-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.component-item {
  padding: 12px;
  border: 1px solid #e8eaed;
  border-radius: 8px;
  cursor: move;
  background: #fafafa;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 10px;
}

.component-item:hover {
  background: #f0f7ff;
  border-color: #91caff;
  transform: translateX(5px);
}

.component-item .component-icon {
  width: 32px;
  height: 32px;
  background: #f0f7ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
  font-size: 18px;
  flex-shrink: 0;
}

.component-info {
  flex: 1;
  min-width: 0;
}

.component-info h4 {
  font-size: 14px;
  margin: 0 0 4px 0;
  font-weight: 500;
  color: #1f1f1f;
}

.component-info p {
  font-size: 12px;
  color: #666;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
