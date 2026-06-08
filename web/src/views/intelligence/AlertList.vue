<template>
  <div>
    <div class="toolbar">
      <h3>预警列表</h3>
      <div class="filter-bar">
        <el-select v-model="filterType" placeholder="预警类型" style="width:120px">
          <el-option label="全部" value="" />
          <el-option label="缺货预警" value="SHORTAGE" />
          <el-option label="库存积压" value="OVERSTOCK" />
          <el-option label="临期预警" value="EXPIRY" />
          <el-option label="呆滞商品" value="SLOW_MOVING" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="状态" style="width:100px">
          <el-option label="全部" value="" />
          <el-option label="未处理" :value="0" />
          <el-option label="已处理" :value="1" />
        </el-select>
        <el-button type="primary" @click="loadAlerts()">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading" ref="alertTable" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="productId" label="商品ID" width="80" />
      <el-table-column prop="productName" label="商品名称" width="150" />
      <el-table-column prop="alertType" label="预警类型" width="100">
        <template #default="{ row }"><el-tag :type="alertTagType(row.alertType)">{{ alertLabel(row.alertType) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="message" label="预警信息" show-overflow-tooltip min-width="300" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '已处理' : '未处理' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="预警时间" width="170" />
      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" link type="primary" @click="handleAlert(row.id)">处理</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div style="margin-top: 16px; display: flex; justify-content: space-between; align-items: center;">
      <el-button 
        type="primary" 
        :disabled="selectedIds.length === 0"
        @click="batchHandle(selectedIds)"
      >
        批量处理选中 ({{ selectedIds.length }})
      </el-button>
      <el-pagination 
        background
        :current-page="pageNum" 
        :page-size="pageSize" 
        :total="total"
        @current-change="(val: number) => loadAlerts(val)" 
        layout="total, prev, pager, next" 
      />
    </div>

    <!-- 预警统计卡片 -->
    <div class="stats-card" v-if="alertStats">
      <h4>预警统计</h4>
      <div class="stats-grid">
        <div class="stat-item danger">
          <div class="stat-icon">📦</div>
          <div class="stat-info">
            <div class="stat-value">{{ alertStats.shortage || 0 }}</div>
            <div class="stat-label">缺货预警</div>
          </div>
        </div>
        <div class="stat-item warning">
          <div class="stat-icon">📊</div>
          <div class="stat-info">
            <div class="stat-value">{{ alertStats.overstock || 0 }}</div>
            <div class="stat-label">库存积压</div>
          </div>
        </div>
        <div class="stat-item danger">
          <div class="stat-icon">⏰</div>
          <div class="stat-info">
            <div class="stat-value">{{ alertStats.expiry || 0 }}</div>
            <div class="stat-label">临期预警</div>
          </div>
        </div>
        <div class="stat-item info">
          <div class="stat-icon">🐢</div>
          <div class="stat-info">
            <div class="stat-value">{{ alertStats.slowMoving || 0 }}</div>
            <div class="stat-label">呆滞商品</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { intelligenceApi, baseApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const tableData = ref<any[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterType = ref('')
const filterStatus = ref('')
const selectedIds = ref<number[]>([])
const alertStats = ref<any>(null)
const productList = ref<any[]>([])
const alertTable = ref<any>(null)

function handleSelectionChange(selection: any[]) {
  selectedIds.value = selection.map(item => item.id)
}

onMounted(async () => {
  await loadProducts()
  await loadAlerts()
  await loadAlertStats()
})

async function loadProducts() {
  const res = await baseApi.getProductPage({ pageNum: 1, pageSize: 1000 })
  productList.value = res.data.list || []
}

function getProductName(productId: number) {
  const product = productList.value.find(p => p.id === productId)
  return product ? product.name : productId
}

async function loadAlerts(val = 1) {
  pageNum.value = val
  loading.value = true
  try {
    const params: any = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (filterType.value) {
      params.alertType = filterType.value
    }
    if (filterStatus.value !== '' && filterStatus.value !== null && !isNaN(Number(filterStatus.value))) {
      params.status = Number(filterStatus.value)
    }
    console.log('Alert API params:', params)
    const res = await intelligenceApi.getAlerts(params)
    console.log('Alert API response:', res)
    console.log('Response status:', res.code)
    console.log('Response data:', res.data)
    
    if (res.code !== 200) {
      console.error('API error:', res.message)
      ElMessage.error('获取预警列表失败: ' + res.message)
      return
    }
    
    const data = res.data.list || []
    console.log('List data:', data)
    tableData.value = data.map((item: any) => ({
      ...item,
      productName: getProductName(item.productId),
      message: formatAlertMessage(item)
    }))
    total.value = res.data.total || 0
    console.log('Total:', total.value)
  } catch (error: any) {
    console.error('Error loading alerts:', error)
    ElMessage.error('获取预警列表失败: ' + (error.message || '网络错误'))
  } finally { 
    loading.value = false 
  }
}

async function loadAlertStats() {
  const res = await intelligenceApi.getAlertCount()
  alertStats.value = res.data
}

function formatAlertMessage(item: any) {
  const originalMsg = item.message || ''
  
  // 中文消息直接返回
  if (/[\u4e00-\u9fa5]/.test(originalMsg)) {
    return originalMsg
  }
  
  // 英文消息转换为中文
  const productName = getProductName(item.productId)
  const alertType = item.alertType
  
  if (alertType === 'SHORTAGE') {
    // 匹配: "ProductName low stock: 80 below safety 20"
    const match = originalMsg.match(/low stock.*?(\d+).*?safety\s*(\d+)/i)
    if (match) {
      return `${productName} 库存不足，当前库存: ${match[1]}, 安全库存: ${match[2]}`
    }
    const match2 = originalMsg.match(/low stock/i)
    if (match2) {
      return `${productName} 库存偏低`
    }
  } else if (alertType === 'OVERSTOCK') {
    // 匹配: "ProductName overstock: 250 above max 200"
    const match = originalMsg.match(/overstock.*?(\d+).*?max\s*(\d+)/i)
    if (match) {
      return `${productName} 库存积压，当前库存: ${match[1]}, 最大库存: ${match[2]}`
    }
    const match2 = originalMsg.match(/high stock/i)
    if (match2) {
      return `${productName} 库存偏高`
    }
  } else if (alertType === 'EXPIRY') {
    // 匹配: "ProductName expiry warning: 15 days remaining"
    const match = originalMsg.match(/expiry.*?(\d+)\s*days?/i)
    if (match) {
      return `${productName} 即将过期，剩余天数: ${match[1]}天`
    }
    const match2 = originalMsg.match(/expiry/i)
    if (match2) {
      return `${productName} 临期预警`
    }
  } else if (alertType === 'SLOW_MOVING') {
    // 匹配: "ProductName slow sales"
    const match = originalMsg.match(/slow sales/i)
    if (match) {
      return `${productName} 销售呆滞`
    }
  }
  
  // 默认返回原始消息
  return originalMsg
}

async function handleAlert(id: number) {
  await intelligenceApi.handleAlert(id)
  ElMessage.success('已处理')
  await loadAlerts(pageNum.value)
  await loadAlertStats()
}

async function batchHandle(ids: number[]) {
  if (ids.length === 0) {
    ElMessage.warning('请选择要处理的预警')
    return
  }
  try {
    for (const id of ids) {
      await intelligenceApi.handleAlert(id)
    }
    ElMessage.success(`已批量处理 ${ids.length} 条预警`)
    selectedIds.value = []
    await loadAlerts(pageNum.value)
    await loadAlertStats()
  } catch (e) {
    ElMessage.error('批量处理失败')
  }
}

function resetFilter() {
  filterType.value = ''
  filterStatus.value = ''
  loadAlerts(1)
}

function alertTagType(t: string) {
  const m: any = { SHORTAGE: 'danger', OVERSTOCK: 'warning', EXPIRY: 'danger', SLOW_MOVING: 'info' }
  return m[t] || 'info'
}

function alertLabel(t: string) {
  const m: any = { SHORTAGE: '缺货', OVERSTOCK: '积压', EXPIRY: '临期', SLOW_MOVING: '呆滞' }
  return m[t] || t
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.filter-bar { display: flex; gap: 12px; align-items: center; }
.stats-card { 
  margin-top: 20px; 
  padding: 16px; 
  background: #f8f9fa; 
  border-radius: 8px;
}
.stats-card h4 { margin: 0 0 12px 0; color: #303133; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-item { 
  display: flex; 
  align-items: center; 
  padding: 12px; 
  background: white; 
  border-radius: 8px;
  border-left: 4px solid;
}
.stat-item.danger { border-color: #f56c6c; }
.stat-item.warning { border-color: #e6a23c; }
.stat-item.info { border-color: #909399; }
.stat-icon { font-size: 24px; margin-right: 12px; }
.stat-value { font-size: 20px; font-weight: bold; color: #303133; }
.stat-label { font-size: 12px; color: #909399; }
</style>