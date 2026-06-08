<template>
  <el-dialog :title="title" :visible="visible" width="700px" @close="handleClose">
    <el-form ref="formRef" :model="formData" label-width="100px" :rules="rules">
      <!-- 基本信息 -->
      <el-form-item label="入库类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择入库类型" :disabled="isView">
          <el-option label="采购入库" value="PURCHASE" />
          <el-option label="退货入库" value="RETURN" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="供应商" prop="supplierId">
        <el-select v-model="formData.supplierId" placeholder="请选择供应商" :disabled="isView">
          <el-option v-for="s in supplierList" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="入库仓库" prop="warehouseId">
        <el-select v-model="formData.warehouseId" placeholder="请选择仓库" :disabled="isView">
          <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" :disabled="isView" />
      </el-form-item>
      
      <!-- 入库明细 -->
      <el-form-item label="入库明细">
        <div class="detail-header">
          <span class="detail-title">商品列表</span>
          <el-button v-if="!isView" type="primary" size="small" @click="addDetail">+ 添加商品</el-button>
        </div>
        <el-table :data="formData.details" border size="small" :disabled="isView">
          <el-table-column prop="productId" label="商品" width="200">
            <template #default="{ row, $index }">
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
          <el-table-column prop="productionDate" label="生产日期" width="120">
            <template #default="{ row }">
              <el-date-picker v-model="row.productionDate" type="date" placeholder="生产日期" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column prop="expiryDate" label="过期日期" width="120">
            <template #default="{ row }">
              <el-date-picker v-model="row.expiryDate" type="date" placeholder="过期日期" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100">
            <template #default="{ row }">
              <el-input v-model.number="row.quantity" type="number" placeholder="数量" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价" width="100">
            <template #default="{ row }">
              <el-input v-model.number="row.unitPrice" type="number" placeholder="单价" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="100">
            <template #default="{ row }">{{ row.quantity * row.unitPrice || 0 }}</template>
          </el-table-column>
          <el-table-column label="操作" width="80" v-if="!isView">
            <template #default="{ $index }">
              <el-button link type="danger" @click="removeDetail($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="total-row">
          <span class="total-label">总金额：</span>
          <span class="total-value">{{ totalAmount }}</span>
        </div>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button v-if="!isView" type="primary" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { baseApi, stockApi } from '@/api/modules'

const props = defineProps<{
  visible: boolean
  isView: boolean
  editData?: number
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', data: any): void
}>()

const formRef = ref()
const supplierList = ref<any[]>([])
const warehouseList = ref<any[]>([])
const productList = ref<any[]>([])

const formData = ref({
  id: null,
  type: 'PURCHASE',
  supplierId: null,
  warehouseId: null,
  remark: '',
  details: [{
    productId: null,
    batchNo: '',
    productionDate: '',
    expiryDate: '',
    quantity: 0,
    unitPrice: 0
  }]
})

const title = computed(() => {
  if (props.isView) return '查看入库单'
  if (props.editData) return '编辑入库单'
  return '新建入库单'
})

const totalAmount = computed(() => {
  return formData.value.details.reduce((sum, item) => {
    return sum + (item.quantity || 0) * (item.unitPrice || 0)
  }, 0).toFixed(2)
})

const rules = {
  type: [{ required: true, message: '请选择入库类型', trigger: 'blur' }],
  warehouseId: [{ required: true, message: '请选择入库仓库', trigger: 'blur' }]
}

onMounted(() => {
  loadSuppliers()
  loadWarehouses()
  loadProducts()
})

watch(() => props.visible, (visible) => {
  if (visible) {
    if (props.editData) {
      loadEditData(props.editData)
    } else if (!props.isView) {
      resetForm()
    }
  }
})

async function loadSuppliers() {
  const res = await baseApi.getSupplierList()
  supplierList.value = res.data || []
}

async function loadWarehouses() {
  const res = await baseApi.getWarehouseList()
  warehouseList.value = res.data || []
}

async function loadProducts() {
  const res = await baseApi.getProductPage({ pageNum: 1, pageSize: 100 })
  productList.value = res.data.list || []
}

async function loadEditData(id: number) {
  const res = await stockApi.getInbound(id)
  const order = res.data
  formData.value = {
    id: order.id,
    type: order.type,
    supplierId: order.supplierId,
    warehouseId: order.warehouseId,
    remark: order.remark,
    details: []
  }
  
  const detailsRes = await stockApi.getInboundDetails(id)
  formData.value.details = detailsRes.data.map((item: any) => ({
    productId: item.productId,
    batchNo: item.batchNo,
    productionDate: item.productionDate,
    expiryDate: item.expiryDate,
    quantity: item.quantity,
    unitPrice: item.unitPrice
  }))
}

function resetForm() {
  formData.value = {
    id: null,
    type: 'PURCHASE',
    supplierId: null,
    warehouseId: null,
    remark: '',
    details: [{
      productId: null,
      batchNo: '',
      productionDate: '',
      expiryDate: '',
      quantity: 0,
      unitPrice: 0
    }]
  }
}

function addDetail() {
  formData.value.details.push({
    productId: null,
    batchNo: '',
    productionDate: '',
    expiryDate: '',
    quantity: 0,
    unitPrice: 0
  })
}

function removeDetail(index: number) {
  if (formData.value.details.length > 1) {
    formData.value.details.splice(index, 1)
  } else {
    ElMessage.warning('至少保留一条商品记录')
  }
}

function getProductName(productId: number) {
  const product = productList.value.find(p => p.id === productId)
  return product ? product.name : '-'
}

async function handleSubmit() {
  if (!formRef.value) return
  
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    
    // 检查明细
    const validDetails = formData.value.details.filter(d => d.productId && d.quantity > 0)
    if (validDetails.length === 0) {
      ElMessage.warning('请至少添加一条有效的商品记录')
      return
    }
    
    try {
      const data = {
        id: formData.value.id,
        type: formData.value.type,
        supplierId: formData.value.supplierId,
        warehouseId: formData.value.warehouseId,
        remark: formData.value.remark,
        details: formData.value.details
      }
      
      if (formData.value.id) {
        await stockApi.updateInbound(formData.value.id, data)
        ElMessage.success('更新成功')
      } else {
        await stockApi.createInbound(data)
        ElMessage.success('创建成功')
      }
      
      emit('submit', data)
      handleClose()
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || '操作失败')
    }
  })
}

function handleClose() {
  emit('close')
}
</script>

<style scoped>
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.detail-title { font-weight: bold; }
.total-row { display: flex; justify-content: flex-end; align-items: center; margin-top: 12px; padding-top: 12px; border-top: 1px solid #eee; }
.total-label { font-weight: bold; }
.total-value { font-size: 18px; font-weight: bold; color: #409eff; margin-left: 8px; }
</style>