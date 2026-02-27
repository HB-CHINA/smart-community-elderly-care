import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const role = ref(userInfo.value.role || '') // 角色：elder/ family/ admin

  // 登录：保存状态+本地持久化
  const login = (tokenVal, userVal) => {
    token.value = tokenVal
    userInfo.value = userVal
    role.value = userVal.role
    localStorage.setItem('token', tokenVal)
    localStorage.setItem('userInfo', JSON.stringify(userVal))
  }

  // 退出：清空状态+本地缓存
  const logout = () => {
    token.value = ''
    userInfo.value = {}
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 更新用户信息（如头像）
  const updateUser = (userVal) => {
    userInfo.value = { ...userInfo.value, ...userVal }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  return { token, userInfo, role, login, logout, updateUser }
}, {
  persist: true // 持久化（可选，替代localStorage手动操作）
})
