import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const isElderlyMode = ref(localStorage.getItem('elderlyMode') === 'true')
  const isHighContrast = ref(localStorage.getItem('highContrast') === 'true')

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const isElder = computed(() => userInfo.value?.role === 'elder')

  // Actions
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const login = async (loginData) => {
    const res = await request.post('/user/login', loginData)
    setToken(res.token)
    setUserInfo(res.user)
    return res
  }

  const register = async (registerData) => {
    const res = await request.post('/user/register', registerData)
    return res
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const toggleElderlyMode = () => {
    isElderlyMode.value = !isElderlyMode.value
    localStorage.setItem('elderlyMode', isElderlyMode.value)
  }

  const toggleHighContrast = () => {
    isHighContrast.value = !isHighContrast.value
    localStorage.setItem('highContrast', isHighContrast.value)
  }

  return {
    token,
    userInfo,
    isElderlyMode,
    isHighContrast,
    isLoggedIn,
    isAdmin,
    isElder,
    setToken,
    setUserInfo,
    login,
    register,
    logout,
    toggleElderlyMode,
    toggleHighContrast
  }
})
