<template>
  <div>
    <div class="toolbar">
      <h3 class="page-title">库存查询</h3>
      <div class="search-bar">
        <el-input v-model="searchForm.keyword" placeholder="商品名称/编码" style="width:220px" clearable @keyup.enter="loadData" />
        <el-select v-model="searchForm.warehouseId" placeholder="选择仓库" style="width:150px" clearable>
          <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
        </el-select>
        <el-button type="primary" @click="loadData(1)">查询</el-button>
        <el-button @click="exportExcel">导出Excel</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <el-card class="stat-card">
        <div class="stat-value">{{ totalQuantity }}</div>
        <div class="stat-label">总库存数量</div>
      </el-card>
      <el-card class="stat-card warning">
        <div class="stat-value">{{ lowStockCount }}</div>
        <div class="stat-label">低于安全库存</div>
      </el-card>
      <el-card class="stat-card danger">
        <div class="stat-value">{{ expiringCount }}</div>
        <div class="stat-label">临期商品</div>
      </el-card>
      <el-card class="stat-card success">
        <div class="stat-value">{{ productCount }}</div>
        <div class="stat-label">商品种类</div>
      </el-card>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading" size="small">
      <el-table-column prop="productCode" label="商品编码" width="120" />
      <el-table-column prop="productName" label="商品名称" />
      <el-table-column prop="productSpec" label="规格型号" width="150" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column prop="batchNo" label="批次号" width="130" />
      <el-table-column prop="quantity" label="库存数量" width="100" align="right">
        <template #default="{ row }">
          <span :class="{ 'low-stock': isLowStock(row) }">{{ row.quantity }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="lockedQuantity" label="锁定库存" width="100" align="right" />
      <el-table-column prop="safetyStock" label="安全库存" width="100" align="right" />
      <el-table-column prop="maxStock" label="最大库存" width="100" align="right" />
      <el-table-column prop="productionDate" label="生产日期" width="120" />
      <el-table-column prop="expiryDate" label="过期日期" width="120">
        <template #default="{ row }">
          <span :class="{ 'expiring': isExpiring(row.expiryDate) }">{{ row.expiryDate || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="最后变动时间" width="170" />
    </el-table>

    <!-- 分页控件 -->
    <el-pagination style="margin-top:16px;justify-content:flex-end" background
      :current-page="pageNum" :page-size="pageSize" :total="total"
      @current-change="loadData" @size-change="loadData" layout="total, prev, pager, next" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { stockApi, baseApi } from '@/api/modules'
import { ElMessage } from 'element-plus'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'

const tableData = ref<any[]>([])
const loading = ref(false)
const warehouseList = ref<any[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  warehouseId: ''
})

const totalQuantity = computed(() => tableData.value.reduce((sum, item) => sum + (item.quantity || 0), 0))
const productCount = computed(() => new Set(tableData.value.map(item => item.productId)).size)
const lowStockCount = computed(() => tableData.value.filter(item => isLowStock(item)).length)
const expiringCount = computed(() => tableData.value.filter(item => isExpiring(item.expiryDate)).length)

onMounted(() => {
  loadData()
  loadWarehouseList()
})

async function loadData(val = 1) {
  pageNum.value = val
  loading.value = true
  try {
    const res = await stockApi.getInventoryPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      warehouseId: searchForm.warehouseId ? Number(searchForm.warehouseId) : undefined,
      keyword: searchForm.keyword || undefined
    })
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function loadWarehouseList() {
  try {
    const res = await baseApi.getWarehouseList()
    warehouseList.value = res.data || []
  } catch (e) {
    console.error('加载仓库列表失败', e)
  }
}

function isLowStock(row: any) {
  return row.quantity && row.safetyStock && row.quantity < row.safetyStock
}

function isExpiring(expiryDate: string) {
  if (!expiryDate) return false
  const expiry = new Date(expiryDate)
  const now = new Date()
  const diffDays = Math.floor((expiry.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  return diffDays > 0 && diffDays <= 30
}

async function exportExcel() {
  loading.value = true
  try {
    // 获取所有数据（不分页）
    const res = await stockApi.getInventoryPage({
      pageNum: 1,
      pageSize: 99999,
      warehouseId: searchForm.warehouseId ? Number(searchForm.warehouseId) : undefined,
      keyword: searchForm.keyword || undefined
    })
    
    const data = res.data.list || []
    
    // 转换数据格式
    const exportData = data.map(item => ({
      '商品编码': item.productCode || '',
      '商品名称': item.productName || '',
      '规格型号': item.productSpec || '',
      '单位': item.unit || '',
      '仓库': item.warehouseName || '',
      '批次号': item.batchNo || '',
      '库存数量': item.quantity || 0,
      '锁定库存': item.lockedQuantity || 0,
      '安全库存': item.safetyStock || 0,
      '最大库存': item.maxStock || 0,
      '生产日期': item.productionDate || '-',
      '过期日期': item.expiryDate || '-',
      '最后变动时间': item.updateTime || '-'
    }))
    
    // 创建工作簿和工作表
    const worksheet = XLSX.utils.json_to_sheet(exportData)
    
    // 设置列宽
    worksheet['!cols'] = [
      { wch: 15 }, { wch: 25 }, { wch: 18 }, { wch: 8 },
      { wch: 15 }, { wch: 15 }, { wch: 12 }, { wch: 12 },
      { wch: 12 }, { wch: 12 }, { wch: 15 }, { wch: 15 },
      { wch: 20 }
    ]
    
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, '库存清单')
    
    // 生成Excel文件并下载
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
    const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const timestamp = new Date().toISOString().slice(0, 10)
    saveAs(blob, `库存清单_${timestamp}.xlsx`)
    
    ElMessage.success('导出成功')
  } catch (e) {
    console.error('导出失败', e)
    ElMessage.error('导出失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { margin: 0; font-size: 18px; }
.search-bar { display: flex; gap: 12px; align-items: center; }
.stats-row { display: flex; gap: 16px; margin-bottom: 16px; }
.stat-card { flex: 1; text-align: center; }
.stat-card.warning { border-color: #e6a23c; }
.stat-card.danger { border-color: #f56c6c; }
.stat-card.success { border-color: #67c23a; }
.stat-value { font-size: 28px; font-weight: bold; color: #409eff; }
.stat-card.warning .stat-value { color: #e6a23c; }
.stat-card.danger .stat-value { color: #f56c6c; }
.stat-card.success .stat-value { color: #67c23a; }
.stat-label { color: #999; margin-top: 8px; }
.low-stock { color: #f56c6c; font-weight: bold; }
.expiring { color: #e6a23c; font-weight: bold; }
</style>
