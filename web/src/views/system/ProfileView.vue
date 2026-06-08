<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <div class="profile-header">
        <div class="avatar-wrapper" @click="triggerUpload">
          <el-avatar 
            :size="120" 
            :src="displayAvatarUrl" 
            icon="UserFilled" 
            class="profile-avatar" 
          />
          <div class="avatar-upload-hint">
            <el-icon size="24"><Camera /></el-icon>
            <span>更换头像</span>
          </div>
        </div>
        <input 
          type="file" 
          ref="avatarInput" 
          class="avatar-input" 
          accept="image/jpeg,image/png,image/gif"
          @change="handleAvatarUpload"
        />
        <div class="profile-info">
          <h2>{{ userInfo.username }}</h2>
          <p class="role">{{ userInfo.roleName || '普通用户' }}</p>
          <p class="dept">{{ userInfo.department || '-' }}</p>
        </div>
      </div>
    </el-card>

    <el-card title="基本信息" class="info-card">
      <el-form :model="userInfo" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名">
              <el-input v-model="userInfo.realName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="userInfo.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门">
              <el-input v-model="userInfo.department" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-input v-model="userInfo.roleName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="card-footer">
        <el-button type="primary" @click="saveProfile">保存修改</el-button>
      </div>
    </el-card>

    <el-card title="修改密码" class="info-card">
      <el-form :model="pwdForm" label-width="100px" class="pwd-form">
        <el-form-item label="原密码">
          <el-input type="password" v-model="pwdForm.oldPassword" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input type="password" v-model="pwdForm.newPassword" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input type="password" v-model="pwdForm.confirmPassword" placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <div class="card-footer">
        <el-button type="warning" @click="changePassword">修改密码</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const baseUrl = import.meta.env.VITE_APP_BASE_URL || 'http://localhost:8080'

const avatarInput = ref<HTMLInputElement | null>(null)
const avatarUrl = ref('')

const displayAvatarUrl = computed(() => {
  if (!avatarUrl.value) return ''
  if (avatarUrl.value.startsWith('http')) return avatarUrl.value
  return baseUrl + avatarUrl.value
})

const userInfo = reactive({
  username: '',
  realName: '',
  email: '',
  phone: '',
  department: '',
  roleName: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
  loadUserInfo()
})

function loadUserInfo() {
  userInfo.username = userStore.username
  userInfo.realName = userStore.realName
  userInfo.email = userStore.email || ''
  userInfo.phone = userStore.phone || ''
  userInfo.department = userStore.department || ''
  userInfo.roleName = userStore.roleName || ''
  avatarUrl.value = userStore.avatar || ''
}

function triggerUpload() {
  avatarInput.value?.click()
}

async function handleAvatarUpload(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }

  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await fetch('/api/auth/avatar', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${userStore.token}`
      },
      body: formData
    })

    const result = await response.json()
    if (result.code === 200) {
      avatarUrl.value = result.data
      userStore.avatar = result.data
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(result.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败')
  }

  target.value = ''
}

async function saveProfile() {
  try {
    await userStore.updateProfile(userInfo)
    ElMessage.success('保存成功')
    setTimeout(() => {
      router.back()
    }, 1000)
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

async function changePassword() {
  if (!pwdForm.oldPassword) {
    ElMessage.warning('请输入原密码')
    return
  }
  if (!pwdForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  try {
    await userStore.changePassword(pwdForm.oldPassword, pwdForm.newPassword)
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
  } catch (e) {
    ElMessage.error('密码修改失败')
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  text-align: center;
  margin-bottom: 20px;
}

.profile-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 30px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.profile-avatar {
  border: 4px solid #409EFF;
  transition: transform 0.2s;
}

.avatar-wrapper:hover .profile-avatar {
  transform: scale(1.05);
}

.avatar-upload-hint {
  position: absolute;
  bottom: -30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 4px;
  color: #409EFF;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-wrapper:hover .avatar-upload-hint {
  opacity: 1;
}

.avatar-input {
  display: none;
}

.profile-info {
  text-align: left;
}

.profile-info h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.role {
  color: #409EFF;
  font-size: 14px;
  margin: 0 0 4px 0;
}

.dept {
  color: #999;
  font-size: 14px;
  margin: 0;
}

.info-card {
  margin-bottom: 20px;
}

.card-footer {
  text-align: right;
  padding-top: 16px;
  border-top: 1px solid #eee;
  margin-top: 16px;
}

.pwd-form {
  max-width: 400px;
}
</style>