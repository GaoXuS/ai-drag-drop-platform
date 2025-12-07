<template>
  <div class="module-list">
    <div class="module-list-header">
      <h3>模組庫</h3>
      <el-button
        type="primary"
        size="small"
        @click="showAddDialog = true"
      >
        添加模組
      </el-button>
    </div>
    <div class="module-list-content">
      <div
        v-for="module in modules"
        :key="module.id"
        class="module-item"
        draggable="true"
        @dragstart="handleDragStart(module, $event)"
        @click="handleAddModule(module.id)"
      >
        <div class="module-icon">
          <el-icon><Box /></el-icon>
        </div>
        <div class="module-info">
          <div class="module-name">{{ module.name }}</div>
          <div class="module-type">{{ module.type }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Box } from '@element-plus/icons-vue'
import { useModuleStore } from '@/stores/moduleStore'
import type { AIModule } from '@/types'

interface Emits {
  (e: 'add-module', moduleId: string): void
}

const emit = defineEmits<Emits>()
const moduleStore = useModuleStore()

const showAddDialog = ref(false)
const modules = computed(() => moduleStore.modules)

const handleDragStart = (module: AIModule, event: DragEvent) => {
  if (event.dataTransfer) {
    event.dataTransfer.setData('moduleId', module.id)
  }
}

const handleAddModule = (moduleId: string) => {
  emit('add-module', moduleId)
}
</script>

<style scoped>
.module-list {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.module-list-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.module-list-header h3 {
  margin: 0;
  font-size: 16px;
}

.module-list-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.module-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.module-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
}

.module-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f9ff;
  border-radius: 6px;
  margin-right: 12px;
  color: #409eff;
  font-size: 20px;
}

.module-info {
  flex: 1;
}

.module-name {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.module-type {
  font-size: 12px;
  color: #909399;
}
</style>

