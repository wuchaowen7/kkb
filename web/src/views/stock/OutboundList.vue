<template>
  <div>
    <div class="toolbar"><h3>出库管理</h3><el-button type="primary" @click="handleAdd">新建出库单</el-button></div>
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="outboundNo" label="出库单号" width="180" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }"><el-tag>{{ row.type === 'SALE' ? '销售出库' : '领用出库' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="department" label="领用部门" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditOpinion" label="审核意见" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="row.status === 'DRAFT'" link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 'PENDING_AUDIT'" link type="success" @click="audit(row.id, true)">通过</el-button>
          <el-button v-if="row.status === 'PENDING_AUDIT'" link type="danger" @click="showRejectDialog(row)">驳回</el-button>
          <el-button v-if="row.status === 'REJECTED'" link type="primary" @click="handleEdit(row)">重新编辑</el-button>
          <el-button link type="primary" @click="showDetail(row)">详情</el-button>
          <el-button v-if="row.status === 'DRAFT'" link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:flex-end" background
      :current-page="pageNum" :page-size="pageSize" :total="total"
      @current-change="loadData" layout="total, prev, pager, next" />
      
    <!-- 驳回对话框 -->
    <el-dialog title="驳回出库单" v-model="rejectDialogVisible" width="400px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="驳回理由">
          <el-input type="textarea" v-model="rejectForm.opinion" :rows="3" placeholder="请输入驳回理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>
    
    <!-- 出库单表单对话框 -->
    <OutboundForm 
      v-model="formVisible" 
      :edit-id="currentEditId"
      :is-view="isViewMode"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { stockApi } from '@/api/modules'
import { ElMessage, ElMessageBox } from 'element-plus'
import OutboundForm from '@/components/stock/OutboundForm.vue'

const tableData = ref<any[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const rejectDialogVisible = ref(false)
const formVisible = ref(false)
const rejectForm = ref({ opinion: '' })
const currentRejectId = ref<number | null>(null)
const currentEditId = ref<number | null>(null)
const isViewMode = ref(false)

onMounted(() => loadData())

async function loadData(val = 1) {
  pageNum.value = val; loading.value = true
  try {
    const res = await stockApi.getOutboundPage({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data.list; total.value = res.data.total
  } finally { loading.value = false }
}

function handleAdd() {
  isViewMode.value = false
  currentEditId.value = null
  formVisible.value = true
}

function handleEdit(row: any) {
  isViewMode.value = false
  currentEditId.value = row.id
  formVisible.value = true
}

async function audit(id: number, approved: boolean) {
  const opinion = approved ? '同意' : '驳回'
  await stockApi.auditOutbound(id, approved, opinion)
  ElMessage.success(approved ? '审核通过' : '已驳回')
  loadData()
}

function showRejectDialog(row: any) {
  currentRejectId.value = row.id
  rejectForm.value = { opinion: '' }
  rejectDialogVisible.value = true
}

async function confirmReject() {
  if (!rejectForm.value.opinion.trim()) {
    ElMessage.warning('请输入驳回理由')
    return
  }
  if (currentRejectId.value) {
    await stockApi.auditOutbound(currentRejectId.value, false, rejectForm.value.opinion)
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    loadData()
  }
}

function showDetail(row: any) {
  isViewMode.value = true
  currentEditId.value = row.id
  formVisible.value = true
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除出库单 ${row.outboundNo} 吗？`,
      '确认删除',
      { type: 'warning' }
    )
    await stockApi.deleteOutbound(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    ElMessage.info('已取消删除')
  }
}

function handleFormSuccess() {
  loadData()
}

function statusLabel(s: string) {
  const map: any = { DRAFT: '草稿', PENDING_AUDIT: '待审核', AUDITED: '已审核', REJECTED: '已驳回' }
  return map[s] || s
}
function statusTagType(s: string) {
  const map: any = { DRAFT: 'info', PENDING_AUDIT: 'warning', AUDITED: 'success', REJECTED: 'danger' }
  return map[s] || 'info'
}
</script>

<style scoped>.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }</style>
