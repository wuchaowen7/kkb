<template>
  <div>
    <div class="toolbar">
      <h3>退库管理</h3>
      <el-button type="primary" @click="handleAdd">新建退库单</el-button>
    </div>
    
    <div class="search-bar">
      <el-input v-model="searchForm.returnNo" placeholder="退库单号" style="width:200px" />
      <el-select v-model="searchForm.status" placeholder="状态" style="width:120px">
        <el-option label="全部" value="" />
        <el-option label="草稿" value="DRAFT" />
        <el-option label="已确认" value="CONFIRMED" />
      </el-select>
      <el-button type="primary" @click="loadData(1)">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="returnNo" label="退库单号" width="180" />
      <el-table-column prop="type" label="类型" width="120">
        <template #default="{ row }">
          <el-tag :type="row.type === 'SALE_RETURN' ? 'warning' : 'info'">
            {{ row.type === 'SALE_RETURN' ? '销售退货' : '采购退货' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="warehouseId" label="仓库" width="120">
        <template #default="{ row }">{{ getWarehouseName(row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column prop="sourceOrderNo" label="来源单号" width="180" />
      <el-table-column prop="totalAmount" label="总金额" width="120">
        <template #default="{ row }">¥{{ row.totalAmount?.toFixed(2) || '0.00' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button v-if="row.status === 'DRAFT'" link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 'DRAFT'" link type="success" @click="confirmReturn(row.id)">确认退库</el-button>
          <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
          <el-button v-if="row.status === 'DRAFT'" link type="danger" @click="deleteReturn(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination style="margin-top:16px;justify-content:flex-end" background
      :current-page="pageNum" :page-size="pageSize" :total="total"
      @current-change="loadData" layout="total, prev, pager, next" />

    <!-- 退库单表单弹窗 -->
    <el-dialog :title="dialogTitle" v-model="formVisible" width="700px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="退库类型">
          <el-select v-model="formData.type" placeholder="请选择退库类型" :disabled="isViewMode">
            <el-option label="销售退货" value="SALE_RETURN" />
            <el-option label="采购退货" value="PURCHASE_RETURN" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源单号">
          <el-input v-model="formData.sourceOrderNo" placeholder="请输入来源单号（如销售单号/采购单号）" :disabled="isViewMode" />
        </el-form-item>
        <el-form-item label="退库仓库">
          <el-select v-model="formData.warehouseId" placeholder="请选择仓库" :disabled="isViewMode">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" :disabled="isViewMode" />
        </el-form-item>
        <el-form-item label="退库明细">
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
            <span class="total-value">¥{{ totalAmount }}</span>
          </div>
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
import { ref, computed, reactive, onMounted } from 'vue'
import { stockApi, baseApi } from '@/api/modules'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref<any[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const warehouseList = ref<any[]>([])
const productList = ref<any[]>([])

const searchForm = reactive({
  returnNo: '',
  status: ''
})

const formVisible = ref(false)
const isViewMode = ref(false)
const currentEditId = ref<number | null>(null)

const formData = ref({
  id: null,
  type: 'SALE_RETURN',
  sourceOrderNo: '',
  warehouseId: null as number | null,
  remark: '',
  details: [{ productId: null as number | null, batchNo: '', quantity: 0, unitPrice: 0 }]
})

const dialogTitle = computed(() => {
  if (isViewMode.value) return '退库单详情'
  if (currentEditId.value) return '编辑退库单'
  return '新建退库单'
})

const totalAmount = computed(() => {
  return formData.value.details.reduce((sum, item) => {
    return sum + (item.quantity || 0) * (item.unitPrice || 0)
  }, 0).toFixed(2)
})

onMounted(() => {
  loadData()
  loadWarehouses()
  loadProducts()
})

async function loadData(val = 1) {
  pageNum.value = val
  loading.value = true
  try {
    const res = await stockApi.getReturnPage({ 
      pageNum: pageNum.value, 
      pageSize: pageSize.value,
      returnNo: searchForm.returnNo,
      status: searchForm.status
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
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

function resetSearch() {
  searchForm.returnNo = ''
  searchForm.status = ''
  loadData(1)
}

function resetForm() {
  formData.value = {
    id: null,
    type: 'SALE_RETURN',
    sourceOrderNo: '',
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

function viewDetail(row: any) {
  isViewMode.value = true
  currentEditId.value = row.id
  loadFormData(row.id)
  formVisible.value = true
}

async function loadFormData(id: number) {
  const res = await stockApi.getReturn(id)
  const data = res.data
  formData.value = {
    id: data.id,
    type: data.type,
    sourceOrderNo: data.sourceOrderNo || '',
    warehouseId: data.warehouseId,
    remark: data.remark || '',
    details: []
  }
  const detailsRes = await stockApi.getReturnDetails(id)
  formData.value.details = detailsRes.data.map((item: any) => ({
    productId: item.productId,
    batchNo: item.batchNo || '',
    quantity: item.quantity,
    unitPrice: item.unitPrice
  }))
}

async function handleSubmit() {
  if (!formData.value.type) {
    ElMessage.warning('请选择退库类型')
    return
  }
  if (!formData.value.warehouseId) {
    ElMessage.warning('请选择退库仓库')
    return
  }
  const validDetails = formData.value.details.filter(d => d.productId && d.quantity > 0)
  if (validDetails.length === 0) {
    ElMessage.warning('请至少添加一条有效的商品记录')
    return
  }
  try {
    const data = {
      id: formData.value.id,
      type: formData.value.type,
      sourceOrderNo: formData.value.sourceOrderNo,
      warehouseId: formData.value.warehouseId,
      remark: formData.value.remark,
      details: formData.value.details
    }
    if (formData.value.id) {
      await stockApi.updateReturn(formData.value.id, data)
      ElMessage.success('更新成功')
    } else {
      await stockApi.createReturn(data)
      ElMessage.success('创建成功')
    }
    formVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

async function confirmReturn(id: number) {
  await stockApi.confirmReturn(id)
  ElMessage.success('退库确认成功')
  loadData()
}

async function deleteReturn(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除退库单 ${row.returnNo} 吗？`,
      '确认删除',
      { type: 'warning' }
    )
    await stockApi.deleteReturn(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    ElMessage.info('已取消删除')
  }
}

function statusLabel(s: string) {
  const map: any = { DRAFT: '草稿', CONFIRMED: '已确认', PENDING: '待确认' }
  return map[s] || s
}

function statusTagType(s: string) {
  const map: any = { DRAFT: 'warning', CONFIRMED: 'success', PENDING: 'info' }
  return map[s] || 'info'
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.search-bar { display: flex; gap: 12px; margin-bottom: 16px; align-items: center; }
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.detail-title { font-weight: bold; }
.total-row { display: flex; justify-content: flex-end; align-items: center; margin-top: 12px; padding-top: 12px; border-top: 1px solid #eee; }
.total-label { font-weight: bold; }
.total-value { font-size: 18px; font-weight: bold; color: #409eff; margin-left: 8px; }
</style>