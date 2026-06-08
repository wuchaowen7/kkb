<template>
  <div class="dashboard">
    <div class="page-header">
      <h3 class="page-title">库存总览</h3>
      <div class="page-actions">
        <el-select v-model="timeRange" style="width:120px" @change="refreshData">
          <el-option :value="7" label="近7天" />
          <el-option :value="30" label="近30天" />
        </el-select>
        <el-button type="primary" @click="refreshData" :loading="loading">
          <span v-if="!loading">刷新数据</span>
          <span v-else>刷新中...</span>
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-blue">
          <div class="stat-icon">📦</div>
          <div class="stat-content">
            <div class="stat-value">{{ dashboard.totalProducts }}</div>
            <div class="stat-label">商品种类</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-green">
          <div class="stat-icon">📊</div>
          <div class="stat-content">
            <div class="stat-value">{{ formatNumber(dashboard.totalStock) }}</div>
            <div class="stat-label">总库存量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-orange">
          <div class="stat-icon">📥</div>
          <div class="stat-content">
            <div class="stat-value">{{ dashboard.pendingInbound }}</div>
            <div class="stat-label">待入库单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-red" @click="goToAlerts">
          <div class="stat-icon">⚠️</div>
          <div class="stat-content">
            <div class="stat-value">{{ dashboard.alertCount }}</div>
            <div class="stat-label">预警数量</div>
          </div>
          <div v-if="dashboard.alertCount > 0" class="alert-badge">待处理</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行统计 -->
    <el-row :gutter="16" class="stat-row" style="margin-top:16px">
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-purple">
          <div class="stat-icon">📈</div>
          <div class="stat-content">
            <div class="stat-value">{{ formatNumber(dashboard.totalOrders) }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-cyan">
          <div class="stat-icon">💰</div>
          <div class="stat-content">
            <div class="stat-value">¥{{ formatNumber(dashboard.totalSales) }}</div>
            <div class="stat-label">销售总额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-pink">
          <div class="stat-icon">🏭</div>
          <div class="stat-content">
            <div class="stat-value">{{ dashboard.warehouseCount }}</div>
            <div class="stat-label">仓库数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-teal">
          <div class="stat-icon">📤</div>
          <div class="stat-content">
            <div class="stat-value">{{ dashboard.pendingOutbound }}</div>
            <div class="stat-label">待出库单</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px">
      <!-- 库存预警分布 -->
      <el-col :xs="24" :sm="24" :md="24" :lg="12">
        <el-card>
          <template #header>
            <span>📊 库存预警分布</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 待处理预警 -->
      <el-col :xs="24" :sm="24" :md="24" :lg="12">
        <el-card>
          <template #header>
            <span>⚠️ 待处理预警</span>
            <el-button type="text" size="small" style="float:right" @click="goToAlerts">查看全部 →</el-button>
          </template>
          <div v-if="alertList.length === 0" class="empty-state">
            <div class="empty-icon">✅</div>
            <div class="empty-text">暂无待处理预警</div>
          </div>
          <el-table v-else :data="alertList" size="small" :highlight-current-row="true">
            <el-table-column prop="productName" label="商品名称" width="120" />
            <el-table-column prop="message" label="预警信息" show-overflow-tooltip />
            <el-table-column prop="alertType" label="类型" width="80">
              <template #default="{ row }">
                <el-tag :type="alertTagType(row.alertType)" size="small">
                  {{ alertTypeLabel(row.alertType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="160" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button type="text" size="small" @click="handleAlert(row.id)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 销售趋势 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :xs="24">
        <el-card>
          <template #header>
            <span>📈 销售趋势</span>
          </template>
          <div ref="trendChartRef" class="chart-container-large"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { reportApi, intelligenceApi } from '@/api/modules'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const timeRange = ref(7)
const dashboard = reactive({ 
  totalProducts: 0, 
  totalStock: 0, 
  pendingInbound: 0, 
  alertCount: 0,
  totalOrders: 0,
  totalSales: 0,
  warehouseCount: 0,
  pendingOutbound: 0
})
const alertList = ref<any[]>([])
const pieChartRef = ref<HTMLDivElement>()
const trendChartRef = ref<HTMLDivElement>()

function formatNumber(num: number): string {
  return num.toLocaleString()
}

function alertTagType(type: string) {
  const map: any = { SHORTAGE: 'danger', OVERSTOCK: 'warning', EXPIRY: 'danger', SLOW_MOVING: 'info' }
  return map[type] || 'info'
}

function alertTypeLabel(type: string) {
  const map: any = { SHORTAGE: '缺货', OVERSTOCK: '积压', EXPIRY: '临期', SLOW_MOVING: '呆滞' }
  return map[type] || type
}

function goToAlerts() {
  window.location.href = '/#/intelligence/alerts'
}

async function handleAlert(id: number) {
  try {
    await intelligenceApi.handleAlert(id)
    ElMessage.success('处理成功')
    await refreshData()
  } catch (error) {
    ElMessage.error('处理失败')
  }
}

async function refreshData() {
  loading.value = true
  try {
    const [dashRes, alertRes, countRes, salesRes] = await Promise.all([
      reportApi.dashboard(),
      intelligenceApi.getAlerts({ status: 0, pageNum: 1, pageSize: 5 }),
      intelligenceApi.getAlertCount(),
      intelligenceApi.salesTrend(timeRange.value)
    ])
    
    Object.assign(dashboard, dashRes.data)
    
    // 处理预警列表
    const alertData = alertRes.data?.list || []
    alertList.value = alertData.map((item: any) => ({
      ...item,
      productName: item.productName || '未知商品'
    }))

    await nextTick()
    renderCharts(countRes.data, salesRes.data)
  } catch (error: any) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败: ' + (error.message || '网络错误'))
  } finally {
    loading.value = false
  }
}

function renderCharts(alertCountData: any, salesData: any) {
  // 饼图 - 库存预警分布
  if (pieChartRef.value) {
    const chart = echarts.init(pieChartRef.value)
    const data = [
      { value: alertCountData?.shortage || 0, name: '缺货预警', itemStyle: { color: '#F56C6C' } },
      { value: alertCountData?.overstock || 0, name: '积压预警', itemStyle: { color: '#E6A23C' } },
      { value: alertCountData?.expiry || 0, name: '临期预警', itemStyle: { color: '#409EFF' } },
      { value: alertCountData?.slowMoving || 0, name: '呆滞预警', itemStyle: { color: '#67C23A' } }
    ]
    
    chart.setOption({
      tooltip: { 
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: '5%',
        top: 'center',
        textStyle: { color: '#666' }
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 16, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: data
      }]
    })
    window.addEventListener('resize', () => chart.resize())
  }

  // 折线图 - 销售趋势
  if (trendChartRef.value && salesData) {
    const chart = echarts.init(trendChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
      xAxis: { 
        type: 'category', 
        data: salesData.map((s: any) => s.date),
        axisLabel: { color: '#666' }
      },
      yAxis: { 
        type: 'value', 
        name: '销售额(元)',
        axisLabel: { color: '#666' },
        splitLine: { lineStyle: { color: '#eee' } }
      },
      series: [{
        name: '销售额',
        type: 'line',
        smooth: true,
        data: salesData.map((s: any) => s.amount),
        areaStyle: { opacity: 0.3, color: '#5470c6' },
        lineStyle: { color: '#5470c6', width: 2 },
        itemStyle: { color: '#5470c6' }
      }]
    })
    window.addEventListener('resize', () => chart.resize())
  }
}

onMounted(() => {
  refreshData()
})
</script>

<style scoped>
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
  display: flex;
  flex-wrap: wrap;
}

.stat-card { 
  text-align: left; 
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
  overflow: hidden;
}

.stat-icon {
  width: 45px;
  height: 45px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}

.stat-blue { border-left: 4px solid #409EFF; }
.stat-green { border-left: 4px solid #67C23A; }
.stat-orange { border-left: 4px solid #E6A23C; }
.stat-red { border-left: 4px solid #F56C6C; }
.stat-purple { border-left: 4px solid #9B59B6; }
.stat-cyan { border-left: 4px solid #00CED1; }
.stat-pink { border-left: 4px solid #E91E63; }
.stat-teal { border-left: 4px solid #009688; }

.stat-blue .stat-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-green .stat-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
.stat-orange .stat-icon { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); }
.stat-red .stat-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-purple .stat-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-cyan .stat-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-pink .stat-icon { background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%); }
.stat-teal .stat-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.stat-content {
  flex: 1;
  overflow: hidden;
}

.stat-value { 
  font-size: 28px; 
  font-weight: bold; 
  margin-bottom: 4px;
}

.stat-label { 
  font-size: 13px; 
  color: #909399; 
}

.stat-blue .stat-value { color: #409EFF; }
.stat-green .stat-value { color: #67C23A; }
.stat-orange .stat-value { color: #E6A23C; }
.stat-red .stat-value { color: #F56C6C; }
.stat-purple .stat-value { color: #9B59B6; }
.stat-cyan .stat-value { color: #00CED1; }
.stat-pink .stat-value { color: #E91E63; }
.stat-teal .stat-value { color: #009688; }

.alert-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #F56C6C;
  color: white;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 10px;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.chart-container-large {
  height: 350px;
  width: 100%;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-text {
  color: #909399;
  font-size: 14px;
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
  
  .stat-label {
    font-size: 12px;
  }
  
  .chart-container {
    height: 250px;
  }
  
  .chart-container-large {
    height: 300px;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 16px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .stat-icon {
    width: 35px;
    height: 35px;
    font-size: 18px;
  }
}
</style>
