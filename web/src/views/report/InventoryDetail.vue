<template>
  <div class="inventory-detail">
    <div class="page-header">
      <h3 class="page-title">库存明细</h3>
      <div class="page-actions">
        <el-button type="primary" @click="loadData">
          <span>刷新数据</span>
        </el-button>
        <el-button @click="exportExcel" :loading="exporting">
          <span v-if="!exporting">导出Excel</span>
          <span v-else>导出中...</span>
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ statistics.totalProducts }}</div>
          <div class="stat-label">商品种类</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ formatNumber(statistics.totalStock) }}</div>
          <div class="stat-label">库存总量</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-warning">
          <div class="stat-value">{{ statistics.lowStockCount }}</div>
          <div class="stat-label">库存不足</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-danger">
          <div class="stat-value">{{ statistics.expiringCount }}</div>
          <div class="stat-label">临期商品</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选条件 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="仓库">
          <el-select v-model="searchForm.warehouseId" placeholder="选择仓库" clearable style="width:150px">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="选择分类" clearable style="width:150px">
            <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品编码">
          <el-input v-model="searchForm.productCode" placeholder="商品编码" clearable style="width:150px" />
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.productName" placeholder="商品名称" clearable style="width:150px" />
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select v-model="searchForm.stockStatus" placeholder="库存状态" clearable style="width:120px">
            <el-option label="正常" value="normal" />
            <el-option label="不足" value="low" />
            <el-option label="积压" value="overstock" />
            <el-option label="临期" value="expiring" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <span>库存明细列表</span>
        <span class="table-info">共 {{ pagination.total }} 条记录</span>
      </template>
      <el-table 
        :data="tableData" 
        border 
        stripe 
        v-loading="loading" 
        size="small"
        :default-sort="{ prop: 'quantity', order: 'descending' }"
        @sort-change="handleSortChange"
      >
        <el-table-column prop="code" label="商品编码" width="120" sortable="custom" fixed />
        <el-table-column prop="name" label="商品名称" min-width="150" sortable="custom" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="spec" label="规格型号" width="120" show-overflow-tooltip />
        <el-table-column prop="unit" label="单位" width="60" align="center" />
        <el-table-column prop="warehouseName" label="仓库" width="100" />
        <el-table-column prop="batchNo" label="批次号" width="120" />
        <el-table-column prop="quantity" label="库存数量" width="100" align="right" sortable="custom">
          <template #default="{ row }">
            <span :class="getStockClass(row)">{{ row.quantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="lockedQuantity" label="锁定数量" width="90" align="right">
          <template #default="{ row }">
            <span v-if="row.lockedQuantity > 0" style="color:#E6A23C">{{ row.lockedQuantity }}</span>
            <span v-else>0</span>
          </template>
        </el-table-column>
        <el-table-column label="可用库存" width="100" align="right">
          <template #default="{ row }">
            {{ row.quantity - (row.lockedQuantity || 0) }}
          </template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" width="90" align="right" />
        <el-table-column prop="maxStock" label="最大库存" width="90" align="right" />
        <el-table-column prop="productionDate" label="生产日期" width="110" />
        <el-table-column prop="expiryDate" label="过期日期" width="110">
          <template #default="{ row }">
            <span v-if="row.expiryDate" :class="getExpiryClass(row.expiryDate)">
              {{ row.expiryDate }}
              <el-tag v-if="getDaysToExpiry(row.expiryDate) <= 30 && getDaysToExpiry(row.expiryDate) > 0" type="danger" size="small">
                {{ getDaysToExpiry(row.expiryDate) }}天
              </el-tag>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row)" size="small">
              {{ getStatusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { reportApi, stockApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const tableData = ref<any[]>([])
const loading = ref(false)
const exporting = ref(false)

const searchForm = reactive({
  warehouseId: '',
  categoryId: '',
  productCode: '',
  productName: '',
  stockStatus: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const sortConfig = reactive({
  prop: 'quantity',
  order: 'descending'
})

const warehouseList = ref<any[]>([])
const categoryList = ref<any[]>([])

const statistics = reactive({
  totalProducts: 0,
  totalStock: 0,
  lowStockCount: 0,
  expiringCount: 0
})

function formatNumber(num: number): string {
  return num?.toLocaleString() || '0'
}

function getStockClass(row: any) {
  if (row.quantity <= row.safetyStock) return 'stock-low'
  if (row.quantity > row.maxStock) return 'stock-over'
  return ''
}

function getExpiryClass(expiryDate: string) {
  const days = getDaysToExpiry(expiryDate)
  if (days <= 0) return 'expired'
  if (days <= 30) return 'expiring'
  return ''
}

function getDaysToExpiry(expiryDate: string): number {
  if (!expiryDate) return 999
  const expiry = new Date(expiryDate)
  const now = new Date()
  return Math.floor((expiry.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
}

function getStatusType(row: any) {
  const days = getDaysToExpiry(row.expiryDate)
  if (days <= 0) return 'danger'
  if (days <= 30) return 'warning'
  if (row.quantity <= row.safetyStock) return 'warning'
  if (row.quantity > row.maxStock) return 'info'
  return 'success'
}

function getStatusText(row: any) {
  const days = getDaysToExpiry(row.expiryDate)
  if (days <= 0) return '已过期'
  if (days <= 30) return '临期'
  if (row.quantity <= row.safetyStock) return '不足'
  if (row.quantity > row.maxStock) return '积压'
  return '正常'
}

async function loadData() {
  loading.value = true
  try {
    const params: any = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      warehouseId: searchForm.warehouseId || undefined,
      categoryId: searchForm.categoryId || undefined,
      productCode: searchForm.productCode || undefined,
      productName: searchForm.productName || undefined
    }

    // 添加排序
    if (sortConfig.prop && sortConfig.order) {
      params.sortField = sortConfig.prop
      params.sortOrder = sortConfig.order === 'ascending' ? 'asc' : 'desc'
    }

    const res = await reportApi.inventoryDetail(params)
    
    // 处理数据
    let data = res.data?.list || res.data || []
    
    // 根据库存状态筛选
    if (searchForm.stockStatus) {
      data = data.filter((item: any) => {
        const days = getDaysToExpiry(item.expiryDate)
        switch (searchForm.stockStatus) {
          case 'low':
            return item.quantity <= item.safetyStock
          case 'overstock':
            return item.quantity > item.maxStock
          case 'expiring':
            return days > 0 && days <= 30
          default:
            return true
        }
      })
    }

    // 计算统计数据
    statistics.totalProducts = new Set(data.map((item: any) => item.productId || item.code)).size
    statistics.totalStock = data.reduce((sum: number, item: any) => sum + (item.quantity || 0), 0)
    statistics.lowStockCount = data.filter((item: any) => item.quantity <= item.safetyStock).length
    statistics.expiringCount = data.filter((item: any) => {
      const days = getDaysToExpiry(item.expiryDate)
      return days > 0 && days <= 30
    }).length

    tableData.value = data
    pagination.total = res.data?.total || data.length

  } catch (error: any) {
    console.error('加载库存明细失败', error)
    ElMessage.error('加载失败: ' + (error.message || '网络错误'))
  } finally {
    loading.value = false
  }
}

async function loadWarehouseList() {
  try {
    const res = await stockApi.getWarehouseList()
    warehouseList.value = res.data || []
  } catch (error) {
    console.error('加载仓库列表失败', error)
  }
}

async function loadCategoryList() {
  try {
    const res = await stockApi.getCategoryList()
    categoryList.value = res.data || []
  } catch (error) {
    console.error('加载分类列表失败', error)
  }
}

function handleSearch() {
  pagination.pageNum = 1
  loadData()
}

function resetSearch() {
  searchForm.warehouseId = ''
  searchForm.categoryId = ''
  searchForm.productCode = ''
  searchForm.productName = ''
  searchForm.stockStatus = ''
  pagination.pageNum = 1
  loadData()
}

function handleSortChange({ prop, order }: any) {
  sortConfig.prop = prop || 'quantity'
  sortConfig.order = order || 'descending'
  loadData()
}

function handlePageChange(page: number) {
  pagination.pageNum = page
  loadData()
}

function handleSizeChange(size: number) {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadData()
}

async function exportExcel() {
  exporting.value = true
  try {
    const response: any = await reportApi.exportExcel('inventory-detail')
    // 创建Blob对象并触发下载
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `inventory_detail_${Date.now()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  loadWarehouseList()
  loadCategoryList()
  loadData()
})
</script>

<style scoped>
.inventory-detail {
  padding: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.page-actions {
  display: flex;
  gap: 12px;
}

.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.stat-warning .stat-value {
  color: #E6A23C;
}

.stat-danger .stat-value {
  color: #F56C6C;
}

.search-card {
  margin-bottom: 16px;
}

.table-card {
  margin-bottom: 16px;
}

.table-info {
  float: right;
  font-size: 13px;
  color: #909399;
  font-weight: normal;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.stock-low {
  color: #F56C6C;
  font-weight: bold;
}

.stock-over {
  color: #909399;
}

.expiring {
  color: #E6A23C;
}

.expired {
  color: #F56C6C;
  text-decoration: line-through;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .stat-value {
    font-size: 24px;
  }
}
</style>
