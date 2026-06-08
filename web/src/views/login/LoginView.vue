<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">智能库存管理系统</h2>
      <p class="login-subtitle">AI Smart Inventory Management</p>
      <el-form ref="formRef" :model="loginForm" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock"
            show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width:100%"
            @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-tips">
        <el-alert title="提示" type="info" :closable="false">
          默认管理员: admin / admin123
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const loginForm = reactive({ username: 'admin', password: 'admin123' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(loginForm)
    await permissionStore.generateRoutes()
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #4a90d9 100%);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}
.login-title {
  text-align: center;
  font-size: 24px;
  color: #303133;
  margin-bottom: 4px;
}
.login-subtitle {
  text-align: center;
  font-size: 13px;
  color: #909399;
  margin-bottom: 30px;
}
.login-tips {
  margin-top: 16px;
}
</style>
