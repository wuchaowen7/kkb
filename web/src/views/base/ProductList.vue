<template>
  <div>
    <div class="toolbar">
      <h3 class="page-title">商品管理</h3>
      <div>
        <el-input v-model="keyword" placeholder="商品名称/编码" style="width:220px" clearable @keyup.enter="search" />
        <el-select v-model="categoryId" placeholder="分类" clearable style="width:150px;margin-left:8px">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-button type="primary" style="margin-left:8px" @click="search">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增商品</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="code" label="商品编码" width="120" />
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="spec" label="规格" width="100" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="safetyStock" label="安全库存" width="90" />
      <el-table-column prop="salePrice" label="售价" width="100" />
      <el-table-column prop="costPrice" label="进价" width="100" />
      <el-table-column prop="expiryDays" label="有效期(天)" width="120" />
      <el-table-column prop="productionDate" label="生产日期" width="120" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="handleStatus(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:flex-end" background
      :current-page="pageNum" :page-size="pageSize" :total="total"
      @current-change="loadData" @size-change="loadData" layout="total, prev, pager, next" />

    <!-- 新增/编辑商品对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="商品编码" prop="code">
          <el-input v-model="formData.code" :disabled="isEdit" placeholder="请输入商品编码" />
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="formData.spec" placeholder="请输入商品规格" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="formData.unit" placeholder="请输入计量单位" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="安全库存" prop="safetyStock">
          <el-input v-model.number="formData.safetyStock" type="number" placeholder="请输入安全库存" />
        </el-form-item>
        <el-form-item label="售价" prop="salePrice">
          <el-input v-model.number="formData.salePrice" type="number" placeholder="请输入售价" />
        </el-form-item>
        <el-form-item label="进价" prop="costPrice">
          <el-input v-model.number="formData.costPrice" type="number" placeholder="请输入进价" />
        </el-form-item>
        <el-form-item label="有效期(天)" prop="expiryDays">
          <el-input v-model.number="formData.expiryDays" type="number" placeholder="请输入有效期天数，入库时自动计算过期日期" />
        </el-form-item>
        <el-form-item label="生产日期" prop="productionDate">
          <el-date-picker v-model="formData.productionDate" type="date" placeholder="请选择生产日期" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { baseApi } from '@/api/modules'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref<any[]>([])
const categories = ref<any[]>([])
const loading = ref(false)
const keyword = ref('')
const categoryId = ref<number>()
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const formData = reactive({
  id: '',
  code: '',
  name: '',
  spec: '',
  unit: '',
  categoryId: '',
  safetyStock: 0,
  salePrice: 0,
  costPrice: 0,
  status: 1,
  remark: ''
})

const dialogTitle = computed(() => isEdit.value ? '编辑商品' : '新增商品')

const formRules = {
  code: [{ required: true, message: '请输入商品编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  spec: [{ required: true, message: '请输入商品规格', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入计量单位', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  safetyStock: [{ required: true, message: '请输入安全库存', trigger: 'blur' }, { type: 'number', min: 0, message: '安全库存不能为负数' }],
  salePrice: [{ required: true, message: '请输入售价', trigger: 'blur' }, { type: 'number', min: 0, message: '售价不能为负数' }],
  costPrice: [{ required: true, message: '请输入进价', trigger: 'blur' }, { type: 'number', min: 0, message: '进价不能为负数' }]
}

onMounted(() => {
  loadData()
  baseApi.getCategoryTree().then(r => categories.value = r.data || [])
})

function search() { pageNum.value = 1; loadData() }

async function loadData(val = 1) {
  pageNum.value = val
  loading.value = true
  try {
    const res = await baseApi.getProductPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value, categoryId: categoryId.value })
    tableData.value = res.data.list
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleAdd() {
  isEdit.value = false
  formData.id = ''
  formData.code = ''
  formData.name = ''
  formData.spec = ''
  formData.unit = ''
  formData.categoryId = ''
  formData.safetyStock = 0
  formData.salePrice = 0
  formData.costPrice = 0
  formData.status = 1
  formData.remark = ''
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  formData.id = row.id
  formData.code = row.code
  formData.name = row.name
  formData.spec = row.spec
  formData.unit = row.unit
  formData.categoryId = row.categoryId
  formData.safetyStock = row.safetyStock
  formData.salePrice = row.salePrice
  formData.costPrice = row.costPrice
  formData.status = row.status
  formData.remark = row.remark || ''
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (isEdit.value) {
      await baseApi.updateProduct(formData.id, formData)
      ElMessage.success('修改成功')
    } else {
      await baseApi.addProduct(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e: any) {
    const errorMsg = e.response?.data?.message || e.message || (isEdit.value ? '修改失败' : '新增失败')
    ElMessage.error(errorMsg)
  }
}

function handleStatus(row: any) {
  const statusText = row.status === 1 ? '下架' : '上架'
  ElMessageBox.confirm(`确定${statusText}该商品？`, '提示', { type: 'warning' })
    .then(async () => {
      await baseApi.updateProductStatus(row.id, row.status === 1 ? 0 : 1)
      ElMessage.success(`${statusText}成功`)
      loadData()
    })
    .catch(() => {})
}

function handleDelete(id: number) {
  ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' })
    .then(async () => {
      await baseApi.deleteProduct(String(id))
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { margin: 0; font-size: 18px; }
</style>
