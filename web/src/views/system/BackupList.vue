<template>
  <div>
    <div class="toolbar">
      <h3>数据备份</h3>
      <el-button type="primary" @click="showCreateModal = true">创建备份</el-button>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading" size="small">
      <el-table-column prop="fileName" label="备份文件名" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="size" label="文件大小" width="120">
        <template #default="{ row }">{{ formatSize(row.size) }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="restoreBackup(row.fileName)">恢复</el-button>
          <el-button link type="danger" @click="deleteBackup(row.fileName)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建备份弹窗 -->
    <el-dialog v-model="showCreateModal" title="创建备份" width="400px">
      <el-form :model="backupForm" label-width="80px">
        <el-form-item label="备注">
          <el-input v-model="backupForm.remark" placeholder="输入备份备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateModal = false">取消</el-button>
        <el-button type="primary" @click="createBackup">确认创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { systemApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const tableData = ref<any[]>([])
const loading = ref(false)
const showCreateModal = ref(false)

const backupForm = reactive({
  remark: ''
})

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await systemApi.getBackupList()
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

async function createBackup() {
  await systemApi.createBackup({ remark: backupForm.remark })
  ElMessage.success('备份创建成功')
  showCreateModal.value = false
  backupForm.remark = ''
  loadData()
}

async function restoreBackup(fileName: string) {
  await systemApi.restoreBackup(fileName)
  ElMessage.success('备份恢复成功')
}

async function deleteBackup(fileName: string) {
  await systemApi.deleteBackup(fileName)
  ElMessage.success('删除成功')
  loadData()
}

function formatSize(bytes: number) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>