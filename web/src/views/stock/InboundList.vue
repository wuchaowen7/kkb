<template>
  <div>
    <div class="toolbar">
      <h3>入库管理</h3>
      <el-button type="primary" @click="openAddForm">新建入库单</el-button>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="inboundNo" label="入库单号" width="180" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }"><el-tag>{{ row.type === 'PURCHASE' ? '采购入库' : '退货入库' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="warehouseId" label="仓库" width="120">
        <template #default="{ row }">{{ getWarehouseName(row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column prop="supplierId" label="供应商" width="150">
        <template #default="{ row }">{{ getSupplierName(row.supplierId) }}</template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'CONFIRMED' ? 'success' : 'warning'">{{ row.status === 'DRAFT' ? '草稿' : '已确认' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button v-if="row.status === 'DRAFT'" link type="primary" @click="openEditForm(row.id)">编辑</el-button>
          <el-button v-if="row.status === 'DRAFT'" link type="danger" @click="handleDelete(row.id)">删除</el-button>
          <el-button v-if="row.status === 'DRAFT'" link type="success" @click="confirmInbound(row.id)">确认入库</el-button>
          <el-button link type="primary" @click="openViewForm(row.id)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:flex-end" background
      :current-page="pageNum" :page-size="pageSize" :total="total"
      @current-change="loadData" layout="total, prev, pager, next" />

    <!-- 入库单表单弹窗 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="showDialog" 
      width="700px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="入库类型">
          <el-select v-model="formData.type" placeholder="请选择入库类型" :disabled="isViewMode">
            <el-option label="采购入库" value="PURCHASE" />
            <el-option label="退货入库" value="RETURN" />
          </el-select>
        </el-form-item>
        <el-form-item label="供应商">
          <el-select v-model="formData.supplierId" placeholder="请选择供应商" :disabled="isViewMode">
            <el-option v-for="s in supplierList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库仓库">
          <el-select v-model="formData.warehouseId" placeholder="请选择仓库" :disabled="isViewMode">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" :disabled="isViewMode" />
        </el-form-item>
        <el-form-item label="入库明细">
          <div class="detail-header">
            <span class="detail-title">商品列表</span>
            <el-button v-if="!isViewMode" type="primary" size="small" @click="addDetail">+ 添加商品</el-button>
          </div>
          <el-table :data="formData.details" border size="small">
            <el-table-column prop="productId" label="商品" width="200">
              <template #default="{ row }">
                <template v-if="isViewMode">{{ getProductName(row.productId) }}</template>
                <template v-else>
                  <el-select v-model="row.productId" placeholder="请选择商品">
                    <el-option v-for="p in productList" :key="p.id" :label="p.name" :value="p.id" />
                  </el-select>
                </template>
              </template>
            </el-table-column>
            <el-table-column prop="batchNo" label="批次号" width="120">
              <template #default="{ row }">
                <el-input v-model="row.batchNo" placeholder="批次号" :disabled="isViewMode" />
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100">
              <template #default="{ row }">
                <el-input v-model.number="row.quantity" type="number" placeholder="数量" :disabled="isViewMode" />
              </template>
            </el-table-column>
            <el-table-column prop="unitPrice" label="单价" width="100">
              <template #default="{ row }">
                <el-input v-model.number="row.unitPrice" type="number" placeholder="单价" :disabled="isViewMode" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" v-if="!isViewMode">
              <template #default="{ $index }">
                <el-button link type="danger" @click="removeDetail($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="total-row">
            <span class="total-label">总金额：</span>
            <span class="total-value">{{ totalAmount }}</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">关闭</el-button>
        <el-button v-if="!isViewMode" type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { stockApi, baseApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const tableData = ref<any[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const warehouseList = ref<any[]>([])
const supplierList = ref<any[]>([])
const productList = ref<any[]>([])

const showDialog = ref(false)
const isViewMode = ref(false)
const currentEditId = ref<number | null>(null)

const formData = ref({
  id: null,
  type: 'PURCHASE',
  supplierId: null,
  warehouseId: null,
  remark: '',
  details: [{ productId: null, batchNo: '', quantity: 0, unitPrice: 0 }]
})

const dialogTitle = computed(() => {
  if (isViewMode.value) return '查看入库单'
  if (currentEditId.value) return '编辑入库单'
  return '新建入库单'
})

const totalAmount = computed(() => {
  return formData.value.details.reduce((sum, item) => {
    return sum + (item.quantity || 0) * (item.unitPrice || 0)
  }, 0).toFixed(2)
})

onMounted(() => {
  loadData()
  loadWarehouses()
  loadSuppliers()
  loadProducts()
})

async function loadData(val = 1) {
  pageNum.value = val; loading.value = true
  try {
    const res = await stockApi.getInboundPage({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data.list; total.value = res.data.total
  } finally { loading.value = false }
}

async function loadWarehouses() {
  const res = await baseApi.getWarehouseList()
  warehouseList.value = res.data || []
}

async function loadSuppliers() {
  const res = await baseApi.getSupplierList()
  supplierList.value = res.data || []
}

async function loadProducts() {
  const res = await baseApi.getProductPage({ pageNum: 1, pageSize: 100 })
  productList.value = res.data.list || []
}

function getWarehouseName(id: number) {
  const warehouse = warehouseList.value.find(w => w.id === id)
  return warehouse ? warehouse.name : id
}

function getSupplierName(id: number) {
  const supplier = supplierList.value.find(s => s.id === id)
  return supplier ? supplier.name : id
}

function getProductName(productId: number) {
  const product = productList.value.find(p => p.id === productId)
  return product ? product.name : '-'
}

function resetForm() {
  formData.value = {
    id: null,
    type: 'PURCHASE',
    supplierId: null,
    warehouseId: null,
    remark: '',
    details: [{ productId: null, batchNo: '', quantity: 0, unitPrice: 0 }]
  }
}

function addDetail() {
  formData.value.details.push({ productId: null, batchNo: '', quantity: 0, unitPrice: 0 })
}

function removeDetail(index: number) {
  if (formData.value.details.length > 1) {
    formData.value.details.splice(index, 1)
  } else {
    ElMessage.warning('至少保留一条商品记录')
  }
}

function openAddForm() {
  resetForm()
  isViewMode.value = false
  currentEditId.value = null
  showDialog.value = true
}

function openEditForm(id: number) {
  isViewMode.value = false
  currentEditId.value = id
  loadFormData(id)
  showDialog.value = true
}

function openViewForm(id: number) {
  isViewMode.value = true
  currentEditId.value = id
  loadFormData(id)
  showDialog.value = true
}



async function loadFormData(id: number) {
  const res = await stockApi.getInbound(id)
  const order = res.data
  formData.value = {
    id: order.id,
    type: order.type,
    supplierId: order.supplierId,
    warehouseId: order.warehouseId,
    remark: order.remark,
    details: []
  }
  const detailsRes = await stockApi.getInboundDetails(id)
  formData.value.details = detailsRes.data.map((item: any) => ({
    productId: item.productId,
    batchNo: item.batchNo,
    quantity: item.quantity,
    unitPrice: item.unitPrice
  }))
}

async function handleDelete(id: number) {
  await stockApi.deleteInbound(id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleSubmit() {
  const validDetails = formData.value.details.filter(d => d.productId && d.quantity > 0)
  if (validDetails.length === 0) {
    ElMessage.warning('请至少添加一条有效的商品记录')
    return
  }
  try {
    const data = {
      id: formData.value.id,
      type: formData.value.type,
      supplierId: formData.value.supplierId,
      warehouseId: formData.value.warehouseId,
      remark: formData.value.remark,
      details: formData.value.details
    }
    if (formData.value.id) {
      await stockApi.updateInbound(formData.value.id, data)
      ElMessage.success('更新成功')
    } else {
      await stockApi.createInbound(data)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

async function confirmInbound(id: number) {
  await stockApi.confirmInbound(id)
  ElMessage.success('入库确认成功')
  loadData()
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.detail-title { font-weight: bold; }
.total-row { display: flex; justify-content: flex-end; align-items: center; margin-top: 12px; padding-top: 12px; border-top: 1px solid #eee; }
.total-label { font-weight: bold; }
.total-value { font-size: 18px; font-weight: bold; color: #409eff; margin-left: 8px; }
</style>