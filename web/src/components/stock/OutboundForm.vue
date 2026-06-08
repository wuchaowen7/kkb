<template>
  <el-dialog :title="title" v-model="dialogVisible" width="700px">
    <el-form ref="formRef" :model="formData" label-width="100px" :rules="rules">
      <!-- 基本信息 -->
      <el-form-item label="出库类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择出库类型" :disabled="isView">
          <el-option label="销售出库" value="SALE" />
          <el-option label="领用出库" value="USE" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="领用部门" prop="department">
        <el-input v-model="formData.department" placeholder="请输入领用部门" :disabled="isView" />
      </el-form-item>
      
      <el-form-item label="出库仓库" prop="warehouseId">
        <el-select v-model="formData.warehouseId" placeholder="请选择仓库" :disabled="isView">
          <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" :disabled="isView" />
      </el-form-item>
      
      <!-- 出库明细 -->
      <el-form-item label="出库明细">
        <div class="detail-header">
          <span class="detail-title">商品列表</span>
          <el-button v-if="!isView" type="primary" size="small" @click="addDetail">+ 添加商品</el-button>
        </div>
        <el-table :data="formData.details" border size="small">
          <el-table-column prop="productId" label="商品" width="200">
            <template #default="{ row }">
              <template v-if="isView">
                {{ getProductName(row.productId) }}
              </template>
              <template v-else>
                <el-select v-model="row.productId" placeholder="请选择商品">
                  <el-option v-for="p in productList" :key="p.id" :label="p.name" :value="p.id" />
                </el-select>
              </template>
            </template>
          </el-table-column>
          <el-table-column prop="batchNo" label="批次号" width="120">
            <template #default="{ row }">
              <el-input v-model="row.batchNo" placeholder="批次号" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100">
            <template #default="{ row }">
              <el-input v-model.number="row.quantity" type="number" placeholder="数量" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" v-if="!isView">
            <template #default="{ $index }">
              <el-button link type="danger" @click="removeDetail($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button v-if="!isView" type="primary" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { baseApi, stockApi } from '@/api/modules'

const props = defineProps<{
  modelValue: boolean
  isView?: boolean
  editId?: number | null
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}>()

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const title = computed(() => {
  if (props.isView) return '出库单详情'
  if (props.editId) return '编辑出库单'
  return '新建出库单'
})

const formRef = ref()
const formData = ref({
  type: 'SALE',
  department: '',
  warehouseId: null as number | null,
  remark: '',
  details: [{ productId: null as number | null, batchNo: '', quantity: 0 }]
})

const rules = {
  type: [{ required: true, message: '请选择出库类型' }],
  warehouseId: [{ required: true, message: '请选择仓库' }]
}

const productList = ref<any[]>([])
const warehouseList = ref<any[]>([])

onMounted(async () => {
  const [products, warehouses] = await Promise.all([
    baseApi.getProductPage({ pageNum: 1, pageSize: 1000 }),
    baseApi.getWarehouseList()
  ])
  productList.value = products.data.list || []
  warehouseList.value = warehouses.data || []
})

watch(() => props.modelValue, async (val) => {
  if (val) {
    if (props.editId) {
      await loadData()
    } else if (!props.isView) {
      resetForm()
    }
  }
})

async function loadData() {
  const res = await stockApi.getOutbound(props.editId!)
  const data = res.data
  formData.value = {
    type: data.type,
    department: data.department,
    warehouseId: data.warehouseId,
    remark: data.remark || '',
    details: data.details?.map((d: any) => ({
      productId: d.productId,
      batchNo: d.batchNo || '',
      quantity: d.quantity
    })) || [{ productId: null, batchNo: '', quantity: 0 }]
  }
}

function resetForm() {
  formData.value = {
    type: 'SALE',
    department: '',
    warehouseId: null,
    remark: '',
    details: [{ productId: null, batchNo: '', quantity: 0 }]
  }
}

function getProductName(id: number) {
  const product = productList.value.find(p => p.id === id)
  return product ? product.name : ''
}

function addDetail() {
  formData.value.details.push({ productId: null, batchNo: '', quantity: 0 })
}

function removeDetail(index: number) {
  if (formData.value.details.length > 1) {
    formData.value.details.splice(index, 1)
  } else {
    ElMessage.warning('至少保留一条明细')
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate()
  if (!valid) return
  
  const data = {
    ...formData.value,
    details: formData.value.details.filter(d => d.productId && d.quantity > 0)
  }
  
  if (data.details.length === 0) {
    ElMessage.warning('请至少添加一条商品明细')
    return
  }
  
  try {
    if (props.editId) {
      await stockApi.updateOutbound(props.editId, data)
      ElMessage.success('修改成功')
    } else {
      await stockApi.createOutbound(data)
      ElMessage.success('创建成功')
    }
    emit('success')
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error('操作失败')
  }
}


</script>

<style scoped>
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.detail-title {
  font-weight: bold;
}
</style>
