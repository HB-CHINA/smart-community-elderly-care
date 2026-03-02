// src/pinia/userStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  // 登录
  const login = (newToken, newUserInfo) => {
    token.value = newToken
    userInfo.value = newUserInfo
    localStorage.setItem('token', newToken)
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    ElMessage.success('退出成功')
  }

  // 更新用户信息
  const updateUser = (newUserInfo) => {
    userInfo.value = { ...userInfo.value, ...newUserInfo }
  }

  return {
    userInfo,
    token,
    login,
    logout,
    updateUser
  }
})
