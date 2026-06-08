<template>
  <div>
    <div class="toolbar">
      <h3>库存盘点</h3>
      <el-button type="primary" @click="handleAdd">新建盘点单</el-button>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="checkNo" label="盘点单号" width="180" />
      <el-table-column prop="warehouseId" label="仓库" width="120">
        <template #default="{ row }">{{ getWarehouseName(row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column prop="totalCount" label="商品种类" width="100" />
      <el-table-column prop="diffCount" label="差异种类" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button v-if="row.status === 'CHECKING'" link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 'CHECKING'" link type="danger" @click="handleDelete(row)">删除</el-button>
          <el-button v-if="row.status === 'CHECKING'" link type="success" @click="confirmCheck(row.id)">确认盘点</el-button>
          <el-button link type="primary" @click="showDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:flex-end" background
      :current-page="pageNum" :page-size="pageSize" :total="total"
      @current-change="loadData" layout="total, prev, pager, next" />

    <!-- 盘点单表单弹窗 -->
    <el-dialog :title="dialogTitle" v-model="formVisible" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="盘点仓库">
          <el-select v-model="formData.warehouseId" placeholder="请选择仓库" :disabled="isViewMode">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" :disabled="isViewMode" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">关闭</el-button>
        <el-button v-if="!isViewMode" type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { stockApi, baseApi } from '@/api/modules'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref<any[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const warehouseList = ref<any[]>([])

const formVisible = ref(false)
const isViewMode = ref(false)
const currentEditId = ref<number | null>(null)

const formData = ref({
  id: null,
  warehouseId: null as number | null,
  remark: ''
})

const dialogTitle = computed(() => {
  if (isViewMode.value) return '盘点单详情'
  if (currentEditId.value) return '编辑盘点单'
  return '新建盘点单'
})

onMounted(() => {
  loadData()
  loadWarehouses()
})

async function loadData(val = 1) {
  pageNum.value = val; loading.value = true
  try {
    const res = await stockApi.getCheckPage({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data.list; total.value = res.data.total
  } finally { loading.value = false }
}

async function loadWarehouses() {
  const res = await baseApi.getWarehouseList()
  warehouseList.value = res.data || []
}

function getWarehouseName(id: number) {
  const warehouse = warehouseList.value.find(w => w.id === id)
  return warehouse ? warehouse.name : id
}

function resetForm() {
  formData.value = {
    id: null,
    warehouseId: null,
    remark: ''
  }
}

function handleAdd() {
  resetForm()
  isViewMode.value = false
  currentEditId.value = null
  formVisible.value = true
}

function handleEdit(row: any) {
  isViewMode.value = false
  currentEditId.value = row.id
  loadFormData(row.id)
  formVisible.value = true
}

function showDetail(row: any) {
  isViewMode.value = true
  currentEditId.value = row.id
  loadFormData(row.id)
  formVisible.value = true
}

async function loadFormData(id: number) {
  const res = await stockApi.getCheck(id)
  const data = res.data
  formData.value = {
    id: data.id,
    warehouseId: data.warehouseId,
    remark: data.remark || ''
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除盘点单 ${row.checkNo} 吗？`,
      '确认删除',
      { type: 'warning' }
    )
    await stockApi.deleteCheck(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    ElMessage.info('已取消删除')
  }
}

async function handleSubmit() {
  if (!formData.value.warehouseId) {
    ElMessage.warning('请选择盘点仓库')
    return
  }
  try {
    const data = {
      warehouseId: formData.value.warehouseId,
      remark: formData.value.remark
    }
    if (formData.value.id) {
      await stockApi.updateCheck(formData.value.id, data)
      ElMessage.success('更新成功')
    } else {
      await stockApi.createCheck(data)
      ElMessage.success('创建成功')
    }
    formVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

async function confirmCheck(id: number) {
  await stockApi.confirmCheck(id)
  ElMessage.success('盘点确认成功')
  loadData()
}

function statusLabel(s: string) {
  const map: any = { CHECKING: '盘点中', CONFIRMED: '已完成' }
  return map[s] || s
}

function statusTagType(s: string) {
  const map: any = { CHECKING: 'info', CONFIRMED: 'success' }
  return map[s] || 'info'
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>