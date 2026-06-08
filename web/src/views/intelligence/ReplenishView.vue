<template>
  <div>
    <div class="toolbar"><h3>智能补货建议</h3></div>
    <el-card>
      <el-form inline>
        <el-form-item label="选择商品">
          <el-select v-model="productId" placeholder="请选择" filterable style="width:300px">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadSuggest">生成建议</el-button></el-form-item>
      </el-form>
      <el-alert v-if="suggestion" type="success" :closable="false" style="margin-top:16px">
        <template #title>
          建议采购量: <strong>{{ suggestion.suggestedQty }}</strong> 件
        </template>
        <p style="margin:4px 0 0">计算公式: {{ suggestion.formula }}</p>
      </el-alert>
    </el-card>
    <el-table :data="replenishList" border stripe style="margin-top:16px" v-loading="loading">
      <el-table-column prop="productId" label="商品ID" width="80" />
      <el-table-column prop="suggestedQty" label="建议采购量" width="120" />
      <el-table-column prop="formula" label="计算说明" />
      <el-table-column label="操作" width="120">
        <template #default>
          <el-button link type="primary">生成采购单</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { intelligenceApi, baseApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const products = ref<any[]>([])
const productId = ref<number>()
const suggestion = ref<any>(null)
const replenishList = ref<any[]>([])
const loading = ref(false)

onMounted(async () => {
  const res = await baseApi.getProductPage({ pageNum: 1, pageSize: 100 })
  products.value = res.data.list || []
})

async function loadSuggest() {
  if (!productId.value) { ElMessage.warning('请选择商品'); return }
  try {
    const res = await intelligenceApi.replenishSuggest(productId.value)
    suggestion.value = res.data
  } catch { ElMessage.error('请求失败') }
}
</script>
<style scoped>.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }</style>
