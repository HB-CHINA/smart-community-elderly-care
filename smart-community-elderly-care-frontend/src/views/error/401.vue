<!-- src/views/error/401.vue -->
<template>
  <div class="error-container">
    <el-card class="error-card">
      <div class="error-icon">
        <el-icon :size="100"><Lock /></el-icon>
      </div>
      <h2 class="error-title">401</h2>
      <p class="error-message">未授权访问</p>
      <p class="error-desc">您没有权限访问此页面，请登录后重试。</p>
      <div class="error-actions">
        <el-button type="primary" @click="$router.push('/login')">去登录</el-button>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f5f5;
}

.error-card {
  text-align: center;
  padding: 40px;
  max-width: 400px;
}

.error-icon {
  color: #f56c6c;
  margin-bottom: 20px;
}

.error-title {
  font-size: 48px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.error-message {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.error-desc {
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}

.error-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>
