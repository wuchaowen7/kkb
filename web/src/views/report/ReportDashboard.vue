<template>
  <div class="big-screen">
    <h2 style="text-align:center;margin-bottom:20px">智能库存管理 - 数据大屏</h2>
    <el-row :gutter="16">
      <el-col :span="6"><el-card class="stat-card"><div class="stat-num">{{ dash.totalProducts }}</div><div>商品种类</div></el-card></el-col>
      <el-col :span="6"><el-card class="stat-card"><div class="stat-num">{{ dash.totalStock }}</div><div>总库存量</div></el-card></el-col>
      <el-col :span="6"><el-card class="stat-card"><div class="stat-num">{{ dash.pendingInbound }}</div><div>待入库</div></el-card></el-col>
      <el-col :span="6"><el-card class="stat-card"><div class="stat-num" style="color:#F56C6C">{{ dash.alertCount }}</div><div>预警数</div></el-card></el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card><div ref="pieChart" style="height:300px"></div></el-card>
      </el-col>
      <el-col :span="12">
        <el-card><div ref="barChart" style="height:300px"></div></el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { reportApi, intelligenceApi } from '@/api/modules'
import * as echarts from 'echarts'

const dash = reactive({ totalProducts: 0, totalStock: 0, pendingInbound: 0, alertCount: 0 })
const pieChart = ref<HTMLDivElement>()
const barChart = ref<HTMLDivElement>()

onMounted(async () => {
  try {
    const [dashRes, countRes] = await Promise.all([reportApi.dashboard(), intelligenceApi.getAlertCount()])
    Object.assign(dash, dashRes.data)
    await nextTick()
    if (pieChart.value) {
      echarts.init(pieChart.value).setOption({
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie', radius: ['45%', '75%'],
          data: [
            { value: countRes.data?.shortage || 0, name: '缺货' },
            { value: countRes.data?.overstock || 0, name: '积压' },
            { value: countRes.data?.expiry || 0, name: '临期' },
            { value: countRes.data?.slowMoving || 0, name: '呆滞' }
          ]
        }]
      })
    }
    if (barChart.value) {
      echarts.init(barChart.value).setOption({
        xAxis: { data: ['1月','2月','3月','4月','5月','6月'] },
        yAxis: {},
        series: [{ name: '入库', type: 'bar', data: [120,200,150,180,220,190] },
                  { name: '出库', type: 'bar', data: [100,180,140,170,200,180] }]
      })
    }
  } catch {}
})
</script>
<style scoped>
.big-screen { background: linear-gradient(135deg, #0a1628, #1a3a5c); min-height:100vh;padding:20px;color:#fff; }
.stat-card { text-align:center;background:rgba(255,255,255,0.1);border:1px solid rgba(255,255,255,0.2); }
.stat-num { font-size:36px;font-weight:bold;color:#409EFF; }
</style>
