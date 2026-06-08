<template>
  <div>
    <div class="toolbar"><h3>操作日志</h3></div>
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="title" label="操作" width="150" />
      <el-table-column prop="operName" label="操作人" width="100" />
      <el-table-column prop="operIp" label="IP" width="130" />
      <el-table-column prop="operUrl" label="请求URL" width="200" show-overflow-tooltip />
      <el-table-column prop="businessType" label="业务类型" width="120" />
      <el-table-column prop="status" label="状态" width="60">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '成功' : '失败' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="costTime" label="耗时(ms)" width="80" />
      <el-table-column prop="createTime" label="时间" width="170" />
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:flex-end" background
      :current-page="pageNum" :page-size="pageSize" :total="total" @current-change="loadData" layout="total, prev, pager, next" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const tableData = ref<any[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

function loadData() {}
</script>
<style scoped>.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }</style>
