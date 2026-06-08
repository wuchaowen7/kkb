<template>
  <div>
    <div class="toolbar">
      <h3>用户管理</h3>
      <el-button type="primary" @click="handleAdd">新增用户</el-button>
    </div>

    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名/姓名/手机号" @keyup.enter="loadData()" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData()">搜索</el-button>
          <el-button @click="searchForm.keyword = ''; loadData()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="department" label="部门" width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="success" @click="assignRoles(row)">分配角色</el-button>
          <el-button link type="warning" @click="resetPwd(row)">重置密码</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination style="margin-top:16px;justify-content:flex-end" background
      :current-page="pageNum" :page-size="pageSize" :total="total" @current-change="loadData" layout="total, prev, pager, next" />

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="formData.department" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input type="password" v-model="formData.password" placeholder="请设置初始密码" />
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

    <el-dialog title="重置密码" v-model="resetPwdVisible" width="400px">
      <el-form :model="resetPwdForm" :rules="resetPwdRules" ref="resetPwdRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" v-model="resetPwdForm.oldPassword" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="resetPwdForm.newPassword" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="resetPwdForm.confirmPassword" placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPwd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="分配角色" v-model="roleDialogVisible" width="450px">
      <el-form>
        <el-form-item label="用户">
          <span style="font-weight: bold">{{ currentUser.realName }}</span>
        </el-form-item>
        <el-form-item label="角色选择">
          <el-checkbox-group v-model="selectedRoles">
            <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id" :value="role.id">
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignRoles">确定</el-button>
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

const searchForm = reactive({
  keyword: ''
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const formData = reactive({
  id: '',
  username: '',
  realName: '',
  phone: '',
  email: '',
  department: '',
  password: '',
  status: 1
})

const resetPwdVisible = ref(false)
const resetPwdRef = ref()
const resetPwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
let resetPwdUserId = ref(0)

const roleDialogVisible = ref(false)
const roleList = ref<any[]>([])
const selectedRoles = ref<number[]>([])
const currentUser = ref<any>({})

const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }],
  password: [{ required: !isEdit.value, message: '请输入密码', trigger: 'blur' }]
}

const resetPwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule: any, value: string, callback: any) => {
      if (value !== resetPwdForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

onMounted(() => {
  loadData()
  loadRoleList()
})

async function loadData(val = 1) {
  pageNum.value = val; loading.value = true
  try {
    const res = await systemApi.getUserPage({ 
      pageNum: pageNum.value, 
      pageSize: pageSize.value,
      keyword: searchForm.keyword 
    })
    tableData.value = res.data.list; total.value = res.data.total
  } finally { loading.value = false }
}

async function loadRoleList() {
  try {
    const res = await systemApi.getRoleList()
    roleList.value = res.data || []
  } catch (e) {
    console.error('加载角色列表失败', e)
  }
}

function handleAdd() {
  isEdit.value = false
  formData.id = ''
  formData.username = ''
  formData.realName = ''
  formData.phone = ''
  formData.email = ''
  formData.department = ''
  formData.password = ''
  formData.status = 1
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  formData.id = row.id
  formData.username = row.username
  formData.realName = row.realName
  formData.phone = row.phone
  formData.email = row.email
  formData.department = row.department || ''
  formData.password = ''
  formData.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (isEdit.value) {
      await systemApi.updateUser(formData.id, formData)
      ElMessage.success('修改成功')
    } else {
      await systemApi.addUser(formData)
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
  ElMessageBox.confirm('确定删除该用户?', '提示', { type: 'warning' })
    .then(async () => { 
      await systemApi.deleteUser(String(id))
      ElMessage.success('删除成功')
      loadData() 
    })
    .catch(() => {})
}

function resetPwd(row: any) {
  if (row.id === 1) {
    ElMessage.warning('超级管理员密码请在个人中心修改')
    return
  }
  resetPwdUserId.value = row.id
  resetPwdForm.oldPassword = ''
  resetPwdForm.newPassword = ''
  resetPwdForm.confirmPassword = ''
  resetPwdVisible.value = true
}

async function handleResetPwd() {
  if (!resetPwdRef.value) return
  const valid = await resetPwdRef.value.validate()
  if (!valid) return

  try {
    await systemApi.resetPwd(resetPwdUserId.value, resetPwdForm)
    ElMessage.success('密码重置成功')
    resetPwdVisible.value = false
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '重置失败')
  }
}

async function assignRoles(row: any) {
  currentUser.value = row
  selectedRoles.value = []
  roleDialogVisible.value = true
  
  try {
    const res = await systemApi.getUserRoles(row.id)
    if (res.data) {
      selectedRoles.value = res.data
    }
  } catch (e) {
    console.error('获取用户角色失败', e)
  }
}

async function handleAssignRoles() {
  try {
    await systemApi.assignRoles(currentUser.value.id, selectedRoles.value)
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error('角色分配失败')
  }
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
</style>
