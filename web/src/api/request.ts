import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 如果是blob类型，直接返回response（文件下载）
    if (response.config.responseType === 'blob') {
      return response
    }
    
    const res = response.data
    if (res.code === 401) {
      localStorage.removeItem('token')
      router.push('/login')
      ElMessage.error(res.message || '登录已过期')
      return Promise.reject(new Error(res.message || '登录已过期'))
    }
    if (res.code === 403) {
      ElMessage.error(res.message || '无权限访问')
      return Promise.reject(new Error(res.message || '无权限'))
    }
    if (res.code !== 200) {
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      const message = error.response.data?.message || '请求失败'
      if (status === 401) {
        localStorage.removeItem('token')
        router.push('/login')
        ElMessage.error('登录已过期')
      } else if (status === 403) {
        ElMessage.error('无权限访问')
      } else if (status === 500) {
        return Promise.reject(new Error(message))
      } else {
        return Promise.reject(new Error(message))
      }
    } else {
      return Promise.reject(new Error('网络异常'))
    }
    return Promise.reject(error)
  }
)

export default request
