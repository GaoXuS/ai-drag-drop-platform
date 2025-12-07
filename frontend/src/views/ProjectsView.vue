<template>
  <div class="projects-view">
    <div class="projects-header">
      <h1>项目开发</h1>
      <div class="header-actions">
        <el-select v-model="filterType" placeholder="全部" style="width: 120px; margin-right: 10px;">
          <el-option label="全部" value="all" />
          <el-option label="项目" value="project" />
        </el-select>
        <el-input
          v-model="searchText"
          placeholder="搜索项目"
          style="width: 300px; margin-right: 10px;"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          项目
        </el-button>
      </div>
    </div>

    <div class="projects-grid" v-loading="projectStore.loading">
      <el-card
        v-for="project in filteredProjects"
        :key="project.id"
        class="project-card"
        shadow="hover"
        @click="openProject(project.id)"
      >
        <div class="project-card-header">
          <div class="project-icon">
            <el-icon v-if="!project.icon" size="40"><Folder /></el-icon>
            <img v-else :src="project.icon" alt="project icon" />
          </div>
          <div class="project-info">
            <h3 class="project-title">
              <el-icon class="check-icon"><Check /></el-icon>
              {{ project.name }}
            </h3>
            <p class="project-description">{{ project.description || '暂无描述' }}</p>
            <div class="project-meta">
              <span class="project-label">项目</span>
              <span class="project-time">
                {{ formatTime(project.updatedAt) }}
              </span>
            </div>
          </div>
        </div>
        <template #footer>
          <div class="project-actions">
            <el-button
              text
              type="primary"
              @click.stop="editProject(project)"
            >
              编辑
            </el-button>
            <el-button
              text
              type="danger"
              @click.stop="deleteProject(project.id)"
            >
              删除
            </el-button>
          </div>
        </template>
      </el-card>

      <el-empty v-if="!projectStore.loading && filteredProjects.length === 0" description="暂无项目" />
    </div>

    <!-- 创建项目对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建项目"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="projectForm" label-width="80px">
        <el-form-item label="项目名称" required>
          <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input
            v-model="projectForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        <el-form-item label="图标URL">
          <el-input v-model="projectForm.icon" placeholder="请输入图标URL（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateProject" :loading="creating">
          创建
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useProjectStore } from '@/stores/projectStore'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Folder, Check } from '@element-plus/icons-vue'
import type { Project } from '@/types'

const router = useRouter()
const projectStore = useProjectStore()

const filterType = ref('all')
const searchText = ref('')
const showCreateDialog = ref(false)
const creating = ref(false)

const projectForm = ref({
  name: '',
  description: '',
  icon: '',
})

const filteredProjects = computed(() => {
  let projects = projectStore.projects

  // 搜索过滤
  if (searchText.value) {
    const keyword = searchText.value.toLowerCase()
    projects = projects.filter(
      (p) =>
        p.name.toLowerCase().includes(keyword) ||
        (p.description && p.description.toLowerCase().includes(keyword))
    )
  }

  return projects.sort((a, b) => {
    return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
  })
})

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    return '今天'
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

const openProject = (projectId: string) => {
  router.push(`/project/${projectId}`)
}

const editProject = (project: Project) => {
  projectForm.value = {
    name: project.name,
    description: project.description || '',
    icon: project.icon || '',
  }
  showCreateDialog.value = true
  // TODO: 实现编辑逻辑
}

const handleCreateProject = async () => {
  if (!projectForm.value.name.trim()) {
    ElMessage.warning('请输入项目名称')
    return
  }

  creating.value = true
  try {
    const project = await projectStore.createProject(projectForm.value)
    ElMessage.success('项目创建成功')
    showCreateDialog.value = false
    resetForm()
    // 创建后自动打开项目
    openProject(project.id)
  } catch (error: any) {
    ElMessage.error(error.message || '创建项目失败')
  } finally {
    creating.value = false
  }
}

const deleteProject = async (projectId: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这个项目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await projectStore.deleteProject(projectId)
    ElMessage.success('删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const resetForm = () => {
  projectForm.value = {
    name: '',
    description: '',
    icon: '',
  }
}

onMounted(async () => {
  await projectStore.fetchProjects()
})
</script>

<style scoped>
.projects-view {
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.projects-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.projects-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
}

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.project-card {
  cursor: pointer;
  transition: all 0.3s;
}

.project-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.project-card-header {
  display: flex;
  gap: 16px;
  padding: 16px;
}

.project-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  border-radius: 8px;
  flex-shrink: 0;
}

.project-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.project-info {
  flex: 1;
  min-width: 0;
}

.project-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
}

.check-icon {
  color: #67c23a;
  font-size: 14px;
}

.project-description {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.project-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

.project-label {
  padding: 2px 8px;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
}

.project-time {
  color: #999;
}

.project-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 8px 16px;
}
</style>

