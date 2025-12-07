<template>
  <el-drawer
    v-model="visible"
    title="模組配置"
    :size="400"
    @close="handleClose"
  >
    <div v-if="node" class="module-editor">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="模組名稱">
          <el-input v-model="moduleName" disabled />
        </el-form-item>
        <el-form-item label="API 地址">
          <el-input v-model="formData.apiConfig.url" />
        </el-form-item>
        <el-form-item label="請求方法">
          <el-select v-model="formData.apiConfig.method">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="超時時間(ms)">
          <el-input-number
            v-model="formData.apiConfig.timeout"
            :min="1000"
            :max="60000"
            :step="1000"
          />
        </el-form-item>
        <el-form-item label="認證類型">
          <el-select v-model="formData.apiConfig.auth.type">
            <el-option label="無" value="none" />
            <el-option label="Basic" value="basic" />
            <el-option label="Bearer" value="bearer" />
            <el-option label="API Key" value="apiKey" />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="formData.apiConfig.auth.type !== 'none'"
          label="認證憑據"
        >
          <el-input
            v-model="formData.apiConfig.auth.credentials"
            type="textarea"
            :rows="3"
            placeholder='JSON 格式，例如: {"token": "xxx"}'
          />
        </el-form-item>
        <el-form-item label="請求頭">
          <el-input
            v-model="formData.apiConfig.headers"
            type="textarea"
            :rows="3"
            placeholder='JSON 格式，例如: {"Content-Type": "application/json"}'
          />
        </el-form-item>
      </el-form>
      <div class="editor-actions">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useModuleStore } from '@/stores/moduleStore'
import type { ModuleNode, APIConfig } from '@/types'

interface Props {
  modelValue: boolean
  node: ModuleNode | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'update', nodeId: string, config: any): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const moduleStore = useModuleStore()
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

const formData = ref<{
  apiConfig: APIConfig
}>({
  apiConfig: {
    url: '',
    method: 'POST',
    timeout: 30000,
    auth: {
      type: 'none',
      credentials: {},
    },
    headers: {},
  },
})

const moduleName = computed(() => {
  if (!props.node) return ''
  const module = moduleStore.getModuleById(props.node.moduleId)
  return module?.name || props.node.moduleId
})

watch(
  () => props.node,
  (node) => {
    if (node) {
      const module = moduleStore.getModuleById(node.moduleId)
      if (module) {
        formData.value = {
          apiConfig: {
            ...module.apiConfig,
            auth: module.apiConfig.auth || {
              type: 'none',
              credentials: {},
            },
          },
        }
      }
    }
  },
  { immediate: true }
)

const handleSave = () => {
  if (!props.node) return

  try {
    // 解析 JSON 字符串
    if (typeof formData.value.apiConfig.headers === 'string') {
      formData.value.apiConfig.headers = JSON.parse(
        formData.value.apiConfig.headers || '{}'
      )
    }
    if (
      typeof formData.value.apiConfig.auth?.credentials === 'string' &&
      formData.value.apiConfig.auth.type !== 'none'
    ) {
      formData.value.apiConfig.auth.credentials = JSON.parse(
        formData.value.apiConfig.auth.credentials || '{}'
      )
    }

    emit('update', props.node.id, {
      apiConfig: formData.value.apiConfig,
    })
    visible.value = false
  } catch (error) {
    console.error('配置格式錯誤:', error)
  }
}

const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.module-editor {
  padding: 20px;
}

.editor-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

