<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useSettingStore } from '@/pinia/settingStore'

// 初始化适老化设置
onMounted(() => {
  const settingStore = useSettingStore()
  settingStore.initAgeFriendly()
})
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  width: 100%;
  height: 100vh;
}
</style>
