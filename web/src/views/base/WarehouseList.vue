<template>
  <div>
    <div class="toolbar">
      <h3 class="page-title">仓库管理</h3>
      <div>
        <el-input v-model="keyword" placeholder="仓库名称" style="width:220px" clearable @keyup.enter="search" />
        <el-button type="primary" style="margin-left:8px" @click="search">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增仓库</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="name" label="仓库名称" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="manager" label="负责人" width="100" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:flex-end" background
      :current-page="pageNum" :page-size="pageSize" :total="total"
      @current-change="loadData" @size-change="loadData" layout="total, prev, pager, next" />

    <!-- 新增/编辑仓库对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="仓库名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入仓库地址" />
        </el-form-item>
        <el-form-item label="负责人" prop="manager">
          <el-input v-model="formData.manager" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
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
const loading = ref(false)
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const formData = reactive({
  id: '',
  name: '',
  address: '',
  manager: '',
  phone: '',
  remark: ''
})

const dialogTitle = computed(() => isEdit.value ? '编辑仓库' : '新增仓库')

const formRules = {
  name: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入仓库地址', trigger: 'blur' }],
  manager: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }]
}

onMounted(() => loadData())

function search() { pageNum.value = 1; loadData() }

async function loadData(val = 1) {
  pageNum.value = val
  loading.value = true
  try {
    const res = await baseApi.getWarehousePage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value })
    tableData.value = res.data.list
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleAdd() {
  isEdit.value = false
  formData.id = ''
  formData.name = ''
  formData.address = ''
  formData.manager = ''
  formData.phone = ''
  formData.remark = ''
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  formData.id = row.id
  formData.name = row.name
  formData.address = row.address
  formData.manager = row.manager
  formData.phone = row.phone
  formData.remark = row.remark || ''
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (isEdit.value) {
      await baseApi.updateWarehouse(formData.id, formData)
      ElMessage.success('修改成功')
    } else {
      await baseApi.addWarehouse(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e: any) {
    const errorMsg = e.response?.data?.message || e.message || (isEdit.value ? '修改失败' : '新增失败')
    ElMessage.error(errorMsg)
  }
}

function handleDelete(id: number) {
  ElMessageBox.confirm('确定删除该仓库？', '提示', { type: 'warning' })
    .then(async () => {
      await baseApi.deleteWarehouse(String(id))
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
