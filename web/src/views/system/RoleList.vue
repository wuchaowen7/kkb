<template>
  <div>
    <div class="toolbar">
      <h3>角色管理</h3>
      <el-button type="primary" @click="handleAdd">新增角色</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="roleCode" label="角色编码" width="180" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="success" @click="assignMenus(row)">分配权限</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination 
      style="margin-top:16px;justify-content:flex-end" 
      background
      :current-page="pageNum" 
      :page-size="pageSize" 
      :total="total" 
      @current-change="loadData" 
      layout="total, prev, pager, next" 
    />

    <!-- 新增/编辑角色对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" :disabled="isEdit" placeholder="请输入角色编码（大写英文）" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入角色描述" 
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog title="分配权限" v-model="menuDialogVisible" width="500px">
      <el-form>
        <el-form-item label="角色">
          <span style="font-weight: bold">{{ currentRole.roleName }}</span>
        </el-form-item>
        <el-form-item label="权限选择">
          <el-tree
            ref="menuTreeRef"
            :data="menuTree"
            :props="{ children: 'children', label: 'menuName' }"
            show-checkbox
            node-key="id"
            :default-checked-keys="checkedMenuIds"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignMenus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { systemApi } from '@/api/modules'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref<any[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const formData = reactive({
  id: '',
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const menuDialogVisible = ref(false)
const menuTreeRef = ref()
const currentRole = ref<any>({})
const checkedMenuIds = ref<number[]>([])
const menuTree = ref<any[]>([])

const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
  loadMenuTree()
})

async function loadData(val = 1) {
  pageNum.value = val
  loading.value = true
  try {
    const res = await systemApi.getRoleList()
    tableData.value = res.data || []
    total.value = tableData.value.length
  } finally {
    loading.value = false
  }
}

async function loadMenuTree() {
  try {
    const res = await systemApi.getMenuTree()
    menuTree.value = res.data || []
  } catch (e) {
    console.error('加载菜单树失败', e)
  }
}

function handleAdd() {
  isEdit.value = false
  formData.id = ''
  formData.roleName = ''
  formData.roleCode = ''
  formData.description = ''
  formData.status = 1
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  formData.id = row.id
  formData.roleName = row.roleName
  formData.roleCode = row.roleCode
  formData.description = row.description || ''
  formData.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (isEdit.value) {
      await systemApi.updateRole(formData.id, formData)
      ElMessage.success('修改成功')
    } else {
      await systemApi.addRole(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(isEdit.value ? '修改失败' : '新增失败')
  }
}

function handleDelete(id: number) {
  if (id === 1) {
    ElMessage.warning('超级管理员不能删除')
    return
  }
  ElMessageBox.confirm('确定删除该角色？', '提示', { type: 'warning' })
    .then(async () => { 
      await systemApi.deleteRole(String(id))
      ElMessage.success('删除成功')
      loadData() 
    })
    .catch(() => {})
}

async function assignMenus(row: any) {
  currentRole.value = row
  checkedMenuIds.value = []
  menuDialogVisible.value = true
  
  // 获取该角色的菜单权限
  try {
    const res = await systemApi.getRoleMenus(row.id)
    if (res.data) {
      checkedMenuIds.value = res.data.map((m: any) => m.id)
    }
  } catch (e) {
    console.error('获取角色菜单失败', e)
  }
}

async function handleAssignMenus() {
  const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
  
  const menuIds = [...checkedKeys] as number[]
  
  try {
    await systemApi.assignMenus(currentRole.value.id, menuIds)
    ElMessage.success('权限分配成功')
    menuDialogVisible.value = false
  } catch (e: any) {
    ElMessage.error('权限分配失败')
  }
}
</script>

<style scoped>
.toolbar { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  margin-bottom: 16px; 
}
</style>
