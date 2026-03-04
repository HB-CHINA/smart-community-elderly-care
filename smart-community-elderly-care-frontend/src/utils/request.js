// src/utils/request.js 修正版本
import axios from 'axios'
import { ElMessage } from 'element-plus' // 适配Element Plus，若用Vue2+ElementUI则导入element-ui

// 创建axios实例
const service = axios.create({
  // 适配Vite/Vue CLI，自动区分开发/生产环境
  baseURL: import.meta.env.VITE_BASE_API || process.env.VUE_APP_BASE_API,
  timeout: 10000, // 延长超时时间，适配养老数据接口
  headers: {
    'Content-Type': 'application/json;charset=utf-8' // 统一请求头，匹配后端接收格式
  }
})

// 请求拦截器：添加token，匹配后端鉴权逻辑
service.interceptors.request.use(
  (config) => {
    // 从本地缓存获取token，匹配后端token传递格式（Bearer + 空格 + token）
    const token = localStorage.getItem('token') || sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    ElMessage.error('请求发送失败，请稍后重试')
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理后端返回格式，适配常见状态码
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 适配后端常见成功状态码（200/0都兼容）
    if (res.code !== 200 && res.code !== 0) {
      ElMessage.error(res.msg || '请求处理失败')
      // 适配后端token过期状态码（401），自动跳登录
      if (res.code === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      return Promise.reject(res)
    }
    return res
  },
  (error) => {
    // 统一处理网络/后端错误
    const errMsg = error.response?.data?.msg || error.message || '服务器异常'
    ElMessage.error(`请求失败：${errMsg}`)
    return Promise.reject(error)
  }
)

export default service
