<template>
  <div class="tasks-view">
    <el-container>
      <el-header>
        <h2>任務執行記錄</h2>
      </el-header>
      <el-main>
        <el-table :data="tasks" v-loading="loading" stripe>
          <el-table-column prop="id" label="任務ID" width="200" />
          <el-table-column prop="workflowId" label="工作流ID" width="200" />
          <el-table-column prop="status" label="狀態" width="120">
            <template #default="{ row }">
              <el-tag
                :type="
                  row.status === 'completed'
                    ? 'success'
                    : row.status === 'failed'
                    ? 'danger'
                    : row.status === 'running'
                    ? 'warning'
                    : 'info'
                "
              >
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="開始時間" width="180" />
          <el-table-column prop="endTime" label="結束時間" width="180" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button size="small" @click="viewLogs(row)">查看日誌</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useWorkflowStore } from '@/stores/workflowStore'
import { ElMessage } from 'element-plus'

const workflowStore = useWorkflowStore()
const tasks = computed(() => workflowStore.tasks)
const loading = computed(() => workflowStore.loading)

onMounted(() => {
  workflowStore.fetchTasks()
})

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '等待中',
    running: '執行中',
    completed: '已完成',
    failed: '失敗',
  }
  return map[status] || status
}

const viewLogs = (task: any) => {
  // TODO: 實現日誌查看功能
  ElMessage.info('日誌查看功能開發中')
}
</script>

<style scoped>
.tasks-view {
  width: 100%;
  height: 100vh;
}

.el-header {
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  display: flex;
  align-items: center;
}

.el-header h2 {
  margin: 0;
}
</style>

