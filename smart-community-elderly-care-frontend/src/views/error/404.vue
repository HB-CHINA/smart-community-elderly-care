<!-- src/views/error/404.vue -->
<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">404</div>
      <div class="error-title">页面不存在</div>
      <div class="error-desc">抱歉，您访问的页面不存在或已被删除</div>
      <el-button type="primary" size="large" @click="goHome">
        <el-icon><HomeFilled /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/pinia/userStore'
import { HomeFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const goHome = () => {
  const role = userStore.userInfo?.role
  if (role === 'elder') {
    router.push('/elder/home')
  } else if (role === 'admin') {
    router.push('/admin/home')
  } else if (role === 'family') {
    router.push('/family/home')
  } else {
    router.push('/login')
  }
}
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.error-content {
  text-align: center;
  color: white;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  line-height: 1;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.error-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 15px;
}

.error-desc {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 30px;
}
</style>
