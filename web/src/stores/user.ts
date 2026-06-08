import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/api/modules'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref<number | null>(null)
  const username = ref('')
  const realName = ref('')
  const email = ref('')
  const phone = ref('')
  const department = ref('')
  const avatar = ref('')
  const roleName = ref('')
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])

  async function login(form: { username: string; password: string }) {
    const res = await authApi.login(form)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    userId.value = res.data.userId
    username.value = res.data.username
    realName.value = res.data.realName
    email.value = res.data.email || ''
    phone.value = res.data.phone || ''
    department.value = res.data.department || ''
    avatar.value = res.data.avatar || ''
    roleName.value = res.data.roleName || ''
    roles.value = res.data.roles || []
    permissions.value = res.data.permissions || []
    return res
  }

  async function getUserInfo() {
    const res = await authApi.getInfo()
    userId.value = res.data.userId
    username.value = res.data.username
    realName.value = res.data.realName
    email.value = res.data.email || ''
    phone.value = res.data.phone || ''
    department.value = res.data.department || ''
    avatar.value = res.data.avatar || ''
    roleName.value = res.data.roleName || ''
    roles.value = res.data.roles || []
    permissions.value = res.data.permissions || []
  }

  async function updateProfile(info: any) {
    await authApi.updateProfile(info)
    realName.value = info.realName
    email.value = info.email
    phone.value = info.phone
    department.value = info.department
  }

  async function changePassword(oldPassword: string, newPassword: string) {
    await authApi.changePassword({ oldPassword, newPassword })
  }

  function logout() {
    token.value = ''
    localStorage.removeItem('token')
    roles.value = []
    permissions.value = []
    router.push('/login')
  }

  function hasPermission(perm: string): boolean {
    return permissions.value.includes(perm) || roles.value.includes('SUPER_ADMIN')
  }

  return { token, userId, username, realName, email, phone, department, avatar, roleName, roles, permissions, login, getUserInfo, logout, hasPermission, updateProfile, changePassword }
})
