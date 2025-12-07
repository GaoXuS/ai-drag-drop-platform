<template>
  <div class="modules-view">
    <el-container>
      <el-header>
        <div class="header-content">
          <h2>模組管理</h2>
          <el-button type="primary" @click="showAddDialog = true">
            添加模組
          </el-button>
        </div>
      </el-header>
      <el-main>
        <el-table :data="modules" v-loading="loading" stripe>
          <el-table-column prop="name" label="名稱" width="200" />
          <el-table-column prop="type" label="類型" width="150" />
          <el-table-column prop="version" label="版本" width="100" />
          <el-table-column prop="apiConfig.url" label="API 地址" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="editModule(row)">編輯</el-button>
              <el-button
                size="small"
                type="danger"
                @click="deleteModule(row.id)"
              >
                刪除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useModuleStore } from '@/stores/moduleStore'
import { ElMessage, ElMessageBox } from 'element-plus'

const moduleStore = useModuleStore()
const modules = computed(() => moduleStore.modules)
const loading = computed(() => moduleStore.loading)
const showAddDialog = ref(false)

onMounted(() => {
  moduleStore.fetchModules()
})

const editModule = (module: any) => {
  // TODO: 實現編輯功能
  ElMessage.info('編輯功能開發中')
}

const deleteModule = async (id: string) => {
  try {
    await ElMessageBox.confirm('確定要刪除此模組嗎？', '提示', {
      type: 'warning',
    })
    await moduleStore.deleteModule(id)
    ElMessage.success('刪除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('刪除失敗')
    }
  }
}
</script>

<style scoped>
.modules-view {
  width: 100%;
  height: 100vh;
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
</style>

