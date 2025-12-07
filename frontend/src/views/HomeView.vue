<template>
  <div class="home-view">
    <el-container>
      <el-header>
        <h1>模块化 AI 拖拽平台</h1>
      </el-header>
      <el-main>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card shadow="hover" @click="goToProjects">
              <template #header>
                <div class="card-header">
                  <el-icon><Folder /></el-icon>
                  <span>项目开发</span>
                </div>
              </template>
              <p>创建和管理 AI 项目，通过拖拽方式设计工作流</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" @click="goToModules">
              <template #header>
                <div class="card-header">
                  <el-icon><Box /></el-icon>
                  <span>模块管理</span>
                </div>
              </template>
              <p>管理 AI 模块配置</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" @click="goToTasks">
              <template #header>
                <div class="card-header">
                  <el-icon><List /></el-icon>
                  <span>任务执行</span>
                </div>
              </template>
              <p>查看任务执行记录</p>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Folder, Box, List } from '@element-plus/icons-vue'

const router = useRouter()

const goToProjects = () => {
  router.push('/projects')
}

const goToModules = () => {
  router.push('/modules')
}

const goToTasks = () => {
  router.push('/tasks')
}
</script>

<style scoped>
.home-view {
  width: 100%;
  height: 100vh;
}

.el-header {
  display: flex;
  align-items: center;
  background-color: #409eff;
  color: white;
}

.el-header h1 {
  margin: 0;
}

.el-main {
  padding: 40px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: bold;
}

.el-card {
  cursor: pointer;
  height: 200px;
}

.el-card p {
  color: #666;
  margin-top: 20px;
}
</style>

