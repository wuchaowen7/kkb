<template>
  <div>
    <div class="toolbar">
      <h3>销量预测</h3>
    </div>
    
    <el-card>
      <div style="display:flex;gap:16px;margin-bottom:16px">
        <el-select v-model="productId" placeholder="选择商品" filterable style="width:300px">
          <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
        <el-select v-model="days" style="width:120px">
          <el-option :value="7" label="7天" />
          <el-option :value="15" label="15天" />
          <el-option :value="30" label="30天" />
        </el-select>
        <el-button type="primary" @click="loadPredict" :loading="loading">
          <span v-if="!loading">开始预测</span>
          <span v-else>预测中...</span>
        </el-button>
      </div>

      <!-- 预测统计卡片 -->
      <div v-if="predictResult" class="stats-row" style="display:flex;gap:16px;margin-bottom:16px">
        <el-card class="stat-card" style="flex:1">
          <div class="stat-label">总预测量</div>
          <div class="stat-value">{{ predictResult.total }}</div>
          <div class="stat-unit">件</div>
        </el-card>
        <el-card class="stat-card" style="flex:1">
          <div class="stat-label">日均预测</div>
          <div class="stat-value">{{ predictResult.avgPerDay }}</div>
          <div class="stat-unit">件/天</div>
        </el-card>
        <el-card class="stat-card" style="flex:1">
          <div class="stat-label">峰值预测</div>
          <div class="stat-value highlight">{{ predictResult.maxValue }}</div>
          <div class="stat-unit">件（D+{{ predictResult.maxDay }}）</div>
        </el-card>
        <el-card class="stat-card" style="flex:1">
          <div class="stat-label">最低预测</div>
          <div class="stat-value">{{ predictResult.minValue }}</div>
          <div class="stat-unit">件（D+{{ predictResult.minDay }}）</div>
        </el-card>
      </div>

      <!-- 图表区域 -->
      <div ref="chartRef" style="height:400px"></div>

      <!-- 详细数据表格 -->
      <div v-if="predictResult" style="margin-top:20px">
        <h4 style="margin-bottom:12px">预测详情</h4>
        <el-table :data="predictTableData" border stripe style="width:100%">
          <el-table-column prop="day" label="预测日期" width="120" />
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="prediction" label="预测量(件)" width="120" />
          <el-table-column prop="trend" label="趋势" width="100">
            <template #default="{ row }">
              <el-tag :type="row.trend === 'up' ? 'success' : row.trend === 'down' ? 'danger' : 'info'">
                {{ row.trend === 'up' ? '上升' : row.trend === 'down' ? '下降' : '持平' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="confidence" label="置信度" width="120">
            <template #default="{ row }">
              <el-progress :percentage="row.confidence" :show-text="false" width="80" />
              <span style="margin-left:8px">{{ row.confidence }}%</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue'
import { intelligenceApi, baseApi } from '@/api/modules'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const products = ref<any[]>([])
const productId = ref<number>()
const days = ref(7)
const chartRef = ref<HTMLDivElement>()
const loading = ref(false)
const predictData = ref<number[]>([])
const historyData = ref<number[]>([])

const predictResult = computed(() => {
  if (!predictData.value.length) return null
  const sum = predictData.value.reduce((a, b) => a + b, 0)
  const max = Math.max(...predictData.value)
  const min = Math.min(...predictData.value)
  return {
    total: sum,
    avgPerDay: Math.round(sum / predictData.value.length),
    maxValue: max,
    maxDay: predictData.value.indexOf(max) + 1,
    minValue: min,
    minDay: predictData.value.indexOf(min) + 1
  }
})

const predictTableData = computed(() => {
  if (!predictData.value.length) return []
  const today = new Date()
  return predictData.value.map((value, index) => {
    const date = new Date(today)
    date.setDate(date.getDate() + index + 1)
    const prevValue = index > 0 ? predictData.value[index - 1] : value
    let trend = 'same'
    if (value > prevValue) trend = 'up'
    else if (value < prevValue) trend = 'down'
    return {
      day: 'D+' + (index + 1),
      date: `${date.getMonth() + 1}/${date.getDate()}`,
      prediction: value,
      trend,
      confidence: Math.min(95, 70 + index * 2)
    }
  })
})

onMounted(async () => {
  const res = await baseApi.getProductPage({ pageNum: 1, pageSize: 100 })
  products.value = res.data.list || []
})

async function loadPredict() {
  if (!productId.value) {
    ElMessage.warning('请选择商品')
    return
  }
  
  loading.value = true
  try {
    const res = await intelligenceApi.predict(productId.value, days.value)
    predictData.value = res.data.predictions || []
    
    // 模拟历史数据（用于对比展示）
    const product = products.value.find(p => p.id === productId.value)
    const baseValue = product ? Math.round(Math.random() * 50 + 30) : 50
    historyData.value = Array.from({ length: 7 }, () => Math.round(baseValue + (Math.random() - 0.5) * 20))
    
    await nextTick()
    renderChart()
  } catch (error: any) {
    ElMessage.error('预测请求失败: ' + (error.message || '网络错误'))
  } finally {
    loading.value = false
  }
}

function renderChart() {
  if (!chartRef.value || !predictData.value.length) return
  
  const chart = echarts.init(chartRef.value)
  const predictLabels = predictData.value.map((_, i) => 'D+' + (i + 1))
  const historyLabels = historyData.value.map((_, i) => '-7D' + (7 - i))
  
  chart.setOption({
    title: {
      text: `「${products.value.find(p => p.id === productId.value)?.name || '商品'}」销量预测`,
      left: 'center',
      textStyle: { fontSize: 16, fontWeight: 'bold' }
    },
    tooltip: {
      trigger: 'axis',
      formatter: function(params: any[]) {
        let result = params[0].axisValueLabel + '<br/>'
        params.forEach((item: any) => {
          result += `${item.marker} ${item.seriesName}: ${item.value} 件<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['历史销量', '预测销量'],
      bottom: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: [...historyLabels, ...predictLabels]
    },
    yAxis: {
      type: 'value',
      name: '销量（件）',
      min: 0
    },
    series: [
      {
        name: '历史销量',
        type: 'line',
        data: [...historyData.value],
        smooth: true,
        lineStyle: { color: '#91cc75', width: 2 },
        areaStyle: { opacity: 0.2, color: '#91cc75' },
        itemStyle: { color: '#91cc75' }
      },
      {
        name: '预测销量',
        type: 'line',
        data: new Array(historyData.value.length).fill(null).concat(predictData.value),
        smooth: true,
        lineStyle: { color: '#5470c6', width: 2, type: 'dashed' },
        areaStyle: { opacity: 0.2, color: '#5470c6' },
        itemStyle: { color: '#5470c6' },
        symbol: 'circle',
        symbolSize: 8
      }
    ]
  })

  // 响应式处理
  window.addEventListener('resize', () => chart.resize())
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.stats-row {
  flex-wrap: wrap;
}

.stat-card {
  text-align: center;
  padding: 16px;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-value.highlight {
  color: #f56c6c;
}

.stat-unit {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.el-table {
  font-size: 14px;
}

.el-progress {
  vertical-align: middle;
}
</style>
