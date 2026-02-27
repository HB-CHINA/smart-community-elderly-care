import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8081/api', // 后端SpringBoot接口根地址
  timeout: 5000, // 贴合论文3.4.1性能需求：500ms内响应
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器：携带Token（贴合论文JWT/Token权限控制）
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    ElMessage.error('请求发送失败：' + error.message)
    return Promise.reject(error)
  }
)

// 响应拦截器：统一解析后端Result<T>、异常处理
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端统一响应码：200成功（论文5.2.1）
    if (res.code !== 200) {
      ElMessage.error(res.message || '操作失败')
      // 401未登录/Token过期：跳转到登录页
      if (res.code === 401) {
        ElMessageBox.confirm('登录状态已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const userStore = useUserStore()
          userStore.logout() // 清空状态
          router.push('/login')
        })
      }
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res.data // 直接返回业务数据
    }
  },
  (error) => {
    ElMessage.error('服务器异常：' + error.message)
    return Promise.reject(error)
  }
)

export default request
