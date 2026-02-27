import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSettingStore = defineStore('setting', () => {
  // 适老化设置：默认大字体16px，普通对比度
  const fontSize = ref(localStorage.getItem('fontSize') || '16px') // 14/16/18px
  const highContrast = ref(localStorage.getItem('highContrast') === 'true' || false) // 高对比度开关

  // 切换字体大小
  const changeFontSize = (size) => {
    fontSize.value = size
    localStorage.setItem('fontSize', size)
    // 动态修改根节点字体大小
    document.documentElement.style.fontSize = size
  }

  // 切换高对比度模式
  const toggleHighContrast = (status) => {
    highContrast.value = status
    localStorage.setItem('highContrast', status)
    // 动态添加/移除高对比度样式类
    const html = document.documentElement
    if (status) {
      html.classList.add('high-contrast')
    } else {
      html.classList.remove('high-contrast')
    }
  }

  // 初始化适老化样式
  const initAgeFriendly = () => {
    document.documentElement.style.fontSize = fontSize.value
    if (highContrast.value) {
      document.documentElement.classList.add('high-contrast')
    }
  }

  return { fontSize, highContrast, changeFontSize, toggleHighContrast, initAgeFriendly }
})
