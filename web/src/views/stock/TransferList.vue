<template>
  <div>
    <div class="toolbar">
      <h3>库存调拨</h3>
      <el-button type="primary" @click="handleAdd">新建调拨单</el-button>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="transferNo" label="调拨单号" width="180" />
      <el-table-column prop="fromWarehouseId" label="源仓库" width="100">
        <template #default="{ row }">{{ getWarehouseName(row.fromWarehouseId) }}</template>
      </el-table-column>
      <el-table-column prop="toWarehouseId" label="目标仓库" width="100">
        <template #default="{ row }">{{ getWarehouseName(row.toWarehouseId) }}</template>
      </el-table-column>
      <el-table-column prop="productId" label="商品" width="150">
        <template #default="{ row }">{{ getProductName(row.productId) }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button v-if="row.status === 'PENDING'" link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 'PENDING'" link type="danger" @click="handleDelete(row)">删除</el-button>
          <el-button v-if="row.status === 'PENDING'" link type="success" @click="confirmTransfer(row.id)">确认调拨</el-button>
          <el-button link type="primary" @click="showDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:flex-end" background
      :current-page="pageNum" :page-size="pageSize" :total="total"
      @current-change="loadData" layout="total, prev, pager, next" />

    <!-- 调拨单表单弹窗 -->
    <el-dialog :title="dialogTitle" v-model="formVisible" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="源仓库">
          <el-select v-model="formData.fromWarehouseId" placeholder="请选择源仓库" :disabled="isViewMode">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标仓库">
          <el-select v-model="formData.toWarehouseId" placeholder="请选择目标仓库" :disabled="isViewMode">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品">
          <el-select v-model="formData.productId" placeholder="请选择商品" :disabled="isViewMode">
            <el-option v-for="p in productList" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model.number="formData.quantity" type="number" placeholder="请输入数量" :disabled="isViewMode" />
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
const productList = ref<any[]>([])

const formVisible = ref(false)
const isViewMode = ref(false)
const currentEditId = ref<number | null>(null)

const formData = ref({
  id: null,
  fromWarehouseId: null as number | null,
  toWarehouseId: null as number | null,
  productId: null as number | null,
  quantity: 0,
  remark: ''
})

const dialogTitle = computed(() => {
  if (isViewMode.value) return '调拨单详情'
  if (currentEditId.value) return '编辑调拨单'
  return '新建调拨单'
})

onMounted(() => {
  loadData()
  loadWarehouses()
  loadProducts()
})

async function loadData(val = 1) {
  pageNum.value = val; loading.value = true
  try {
    const res = await stockApi.getTransferPage({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data.list; total.value = res.data.total
  } finally { loading.value = false }
}

async function loadWarehouses() {
  const res = await baseApi.getWarehouseList()
  warehouseList.value = res.data || []
}

async function loadProducts() {
  const res = await baseApi.getProductPage({ pageNum: 1, pageSize: 100 })
  productList.value = res.data.list || []
}

function getWarehouseName(id: number) {
  const warehouse = warehouseList.value.find(w => w.id === id)
  return warehouse ? warehouse.name : id
}

function getProductName(productId: number) {
  const product = productList.value.find(p => p.id === productId)
  return product ? product.name : productId
}

function resetForm() {
  formData.value = {
    id: null,
    fromWarehouseId: null,
    toWarehouseId: null,
    productId: null,
    quantity: 0,
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
  const res = await stockApi.getTransfer(id)
  const data = res.data
  formData.value = {
    id: data.id,
    fromWarehouseId: data.fromWarehouseId,
    toWarehouseId: data.toWarehouseId,
    productId: data.productId,
    quantity: data.quantity,
    remark: data.remark || ''
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除调拨单 ${row.transferNo} 吗？`,
      '确认删除',
      { type: 'warning' }
    )
    await stockApi.deleteTransfer(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    ElMessage.info('已取消删除')
  }
}

async function handleSubmit() {
  if (!formData.value.fromWarehouseId) {
    ElMessage.warning('请选择源仓库')
    return
  }
  if (!formData.value.toWarehouseId) {
    ElMessage.warning('请选择目标仓库')
    return
  }
  if (formData.value.fromWarehouseId === formData.value.toWarehouseId) {
    ElMessage.warning('源仓库和目标仓库不能相同')
    return
  }
  if (!formData.value.productId) {
    ElMessage.warning('请选择商品')
    return
  }
  if (!formData.value.quantity || formData.value.quantity <= 0) {
    ElMessage.warning('请输入有效的数量')
    return
  }
  try {
    const data = {
      fromWarehouseId: formData.value.fromWarehouseId,
      toWarehouseId: formData.value.toWarehouseId,
      productId: formData.value.productId,
      quantity: formData.value.quantity,
      remark: formData.value.remark
    }
    if (formData.value.id) {
      await stockApi.updateTransfer(formData.value.id, data)
      ElMessage.success('更新成功')
    } else {
      await stockApi.createTransfer(data)
      ElMessage.success('创建成功')
    }
    formVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

async function confirmTransfer(id: number) {
  await stockApi.confirmTransfer(id)
  ElMessage.success('调拨确认成功')
  loadData()
}

function statusLabel(s: string) {
  const map: any = { PENDING: '待确认', CONFIRMED: '已完成' }
  return map[s] || s
}

function statusTagType(s: string) {
  const map: any = { PENDING: 'warning', CONFIRMED: 'success' }
  return map[s] || 'info'
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>