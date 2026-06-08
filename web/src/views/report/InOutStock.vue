<template>
  <div>
    <div class="toolbar">
      <h3>进销存报表</h3>
      <div>
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" />
        <el-button type="primary" style="margin-left:8px" @click="loadData">查询</el-button>
        <el-button @click="exportExcel">导出Excel</el-button>
      </div>
    </div>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card><template #header>入库汇总</template>
          <el-table :data="inboundList" size="small">
            <el-table-column prop="productId" label="商品ID" width="80" />
            <el-table-column prop="quantity" label="入库数量" width="100" />
            <el-table-column prop="totalAmount" label="金额" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card><template #header>出库汇总</template>
          <el-table :data="outboundList" size="small">
            <el-table-column prop="productId" label="商品ID" width="80" />
            <el-table-column prop="quantity" label="出库数量" width="100" />
            <el-table-column prop="totalAmount" label="金额" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { reportApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const dateRange = ref<[Date, Date]>()
const inboundList = ref<any[]>([])
const outboundList = ref<any[]>([])

async function loadData() {
  // TODO: load from API
  ElMessage.info('报表加载中...')
}

async function exportExcel() {
  const res = await reportApi.exportExcel('in-out-stock')
  ElMessage.success(res.data || '导出中')
}
</script>
<style scoped>.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }</style>
