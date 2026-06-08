<template>
  <div>
    <div class="toolbar">
      <h3>智能分析</h3>
      <div style="display:flex;gap:12px">
        <el-select v-model="timeRange" style="width:150px">
          <el-option :value="7" label="近7天" />
          <el-option :value="30" label="近30天" />
          <el-option :value="90" label="近90天" />
        </el-select>
        <el-button type="primary" @click="refreshData" :loading="loading">
          <span v-if="!loading">刷新数据</span>
          <span v-else>刷新中...</span>
        </el-button>
      </div>
    </div>

    <!-- 统计概览卡片 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon sales-icon">📊</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalSales }}</div>
            <div class="stat-label">总销售额</div>
            <div class="stat-change" :class="stats.salesChange >= 0 ? 'up' : 'down'">
              {{ stats.salesChange >= 0 ? '↑' : '↓' }} {{ Math.abs(stats.salesChange) }}%
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon order-icon">📦</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalOrders }}</div>
            <div class="stat-label">订单数量</div>
            <div class="stat-change" :class="stats.orderChange >= 0 ? 'up' : 'down'">
              {{ stats.orderChange >= 0 ? '↑' : '↓' }} {{ Math.abs(stats.orderChange) }}%
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon product-icon">🛒</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalProducts }}</div>
            <div class="stat-label">商品种类</div>
            <div class="stat-change" :class="stats.productChange >= 0 ? 'up' : 'down'">
              {{ stats.productChange >= 0 ? '↑' : '↓' }} {{ Math.abs(stats.productChange) }}%
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon alert-icon">⚠️</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.alertCount }}</div>
            <div class="stat-label">预警数量</div>
            <div class="stat-change" :class="stats.alertChange >= 0 ? 'up' : 'down'">
              {{ stats.alertChange >= 0 ? '↑' : '↓' }} {{ Math.abs(stats.alertChange) }}%
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容区域 -->
    <el-row :gutter="16">
      <!-- 左侧：热销商品排行 -->
      <el-col :span="10">
        <el-card>
          <template #header>
            <span>🔥 热销商品排行</span>
            <span style="float:right;font-size:12px;color:#999">TOP 10</span>
          </template>
          <el-table :data="topSelling" size="small" :highlight-current-row="true">
            <el-table-column type="index" label="#" width="50">
              <template #default="{ $index }">
                <span :class="['rank-badge', getRankClass($index)]">{{ $index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="商品名称" min-width="120" />
            <el-table-column prop="saleCount" label="销量" width="100" />
            <el-table-column prop="revenue" label="销售额" width="120">
              <template #default="{ row }">¥{{ row.revenue.toLocaleString() }}</template>
            </el-table-column>
            <el-table-column prop="growth" label="增长率" width="100">
              <template #default="{ row }">
                <el-tag :type="row.growth >= 0 ? 'success' : 'danger'" size="small">
                  {{ row.growth >= 0 ? '↑' : '↓' }} {{ Math.abs(row.growth) }}%
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧：库存周转率 -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <span>📈 库存周转率分析</span>
          </template>
          <div ref="turnoverChart" style="height:350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：销售趋势和品类分析 -->
    <el-row :gutter="16" style="margin-top:16px">
      <!-- 销售趋势 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>📊 销售趋势</span>
          </template>
          <div ref="salesChart" style="height:300px"></div>
        </el-card>
      </el-col>

      <!-- 品类分布 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>🏷️ 品类销售分布</span>
          </template>
          <div ref="categoryChart" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预警概览 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>⚠️ 预警概览</span>
            <el-button type="text" size="small" style="float:right" @click="goToAlerts">查看全部 →</el-button>
          </template>
          <el-row :gutter="12">
            <el-col :span="6" v-for="item in alertStats" :key="item.type">
              <div class="alert-stat-item">
                <div class="alert-stat-icon">{{ item.icon }}</div>
                <div class="alert-stat-info">
                  <div class="alert-stat-value">{{ item.count }}</div>
                  <div class="alert-stat-label">{{ item.label }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { intelligenceApi } from '@/api/modules'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const timeRange = ref(7)
const loading = ref(false)
const topSelling = ref<any[]>([])
const turnoverData = ref<any[]>([])
const salesData = ref<any[]>([])
const categoryData = ref<any[]>([])
const turnoverChart = ref<HTMLDivElement>()
const salesChart = ref<HTMLDivElement>()
const categoryChart = ref<HTMLDivElement>()

const stats = ref({
  totalSales: 0,
  totalOrders: 0,
  totalProducts: 0,
  alertCount: 0,
  salesChange: 0,
  orderChange: 0,
  productChange: 0,
  alertChange: 0
})

const alertStats = ref([
  { type: 'stock_low', icon: '📉', label: '库存不足', count: 0 },
  { type: 'stock_high', icon: '📈', label: '库存积压', count: 0 },
  { type: 'expire', icon: '⏰', label: '即将过期', count: 0 },
  { type: 'sales', icon: '💸', label: '销售呆滞', count: 0 }
])

function getRankClass(index: number) {
  if (index === 0) return 'rank-gold'
  if (index === 1) return 'rank-silver'
  if (index === 2) return 'rank-bronze'
  return ''
}

function goToAlerts() {
  window.location.href = '/#/intelligence/alerts'
}

async function loadData() {
  loading.value = true
  try {
    const [sellRes, turnRes, salesRes, categoryRes, statsRes, alertRes] = await Promise.all([
      intelligenceApi.topSelling(10),
      intelligenceApi.turnover(),
      intelligenceApi.salesTrend(timeRange.value),
      intelligenceApi.categoryAnalysis(),
      intelligenceApi.stats(),
      intelligenceApi.alertStats()
    ])

    topSelling.value = sellRes.data || []
    turnoverData.value = turnRes.data || []
    salesData.value = salesRes.data || []
    categoryData.value = categoryRes.data || []
    
    if (statsRes.data) {
      stats.value = {
        totalSales: statsRes.data.totalSales || 0,
        totalOrders: statsRes.data.totalOrders || 0,
        totalProducts: statsRes.data.totalProducts || 0,
        alertCount: statsRes.data.alertCount || 0,
        salesChange: statsRes.data.salesChange || 0,
        orderChange: statsRes.data.orderChange || 0,
        productChange: statsRes.data.productChange || 0,
        alertChange: statsRes.data.alertChange || 0
      }
    }

    if (alertRes.data) {
      alertStats.value = [
        { type: 'stock_low', icon: '📉', label: '库存不足', count: alertRes.data.stockLow || 0 },
        { type: 'stock_high', icon: '📈', label: '库存积压', count: alertRes.data.stockHigh || 0 },
        { type: 'expire', icon: '⏰', label: '即将过期', count: alertRes.data.expire || 0 },
        { type: 'sales', icon: '💸', label: '销售呆滞', count: alertRes.data.salesSlow || 0 }
      ]
    }

    await nextTick()
    renderCharts()
  } catch (error: any) {
    ElMessage.error('加载数据失败: ' + (error.message || '网络错误'))
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  // 库存周转率图表
  if (turnoverChart.value) {
    const chart = echarts.init(turnoverChart.value)
    chart.setOption({
      title: { text: '各品类库存周转率', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: turnoverData.value.map((t: any) => t.name),
        axisLabel: { rotate: 30 }
      },
      yAxis: { type: 'value', name: '周转率(次/月)' },
      series: [{
        type: 'bar',
        data: turnoverData.value.map((t: any) => t.rate),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }]
    })
    window.addEventListener('resize', () => chart.resize())
  }

  // 销售趋势图表
  if (salesChart.value && salesData.value.length) {
    const chart = echarts.init(salesChart.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: salesData.value.map((s: any) => s.date) },
      yAxis: { type: 'value', name: '销售额(元)' },
      series: [{
        name: '销售额',
        type: 'line',
        smooth: true,
        data: salesData.value.map((s: any) => s.amount),
        areaStyle: { opacity: 0.3, color: '#5470c6' },
        lineStyle: { color: '#5470c6', width: 2 },
        itemStyle: { color: '#5470c6' }
      }]
    })
    window.addEventListener('resize', () => chart.resize())
  }

  // 品类分布图表
  if (categoryChart.value && categoryData.value.length) {
    const chart = echarts.init(categoryChart.value)
    chart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: '5%',
        top: 'center'
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: categoryData.value.map((c: any, i: number) => ({
          value: c.value,
          name: c.name,
          itemStyle: { color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272'][i % 6] }
        }))
      }]
    })
    window.addEventListener('resize', () => chart.resize())
  }
}

const refreshData = loadData

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.sales-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.order-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.product-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.alert-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.stat-info { flex: 1; }

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.stat-change {
  font-size: 12px;
  margin-top: 4px;
}

.stat-change.up { color: #67c23a; }
.stat-change.down { color: #f56c6c; }

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #f5f5f5;
  color: #666;
  font-size: 12px;
  font-weight: bold;
}

.rank-gold { background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%); color: #fff; }
.rank-silver { background: linear-gradient(135deg, #c0c0c0 0%, #a0a0a0 100%); color: #fff; }
.rank-bronze { background: linear-gradient(135deg, #cd7f32 0%, #b87333 100%); color: #fff; }

.alert-stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.alert-stat-icon { font-size: 28px; }

.alert-stat-info { flex: 1; }

.alert-stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.alert-stat-label {
  font-size: 12px;
  color: #999;
}
</style>
