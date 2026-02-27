<template>
  <div class="error-container">
    <el-card class="error-card">
      <h2 class="error-code">401</h2>
      <p class="error-desc">登录状态已过期，请重新登录</p>
      <el-button type="primary" @click="goToLogin">返回登录</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.error-card {
  width: 400px;
  text-align: center;
  padding: 30px;
}

.error-code {
  font-size: 60px;
  color: #f56c6c;
  margin-bottom: 20px;
}

.error-desc {
  font-size: 18px;
  margin-bottom: 30px;
  color: #666;
}
</style>
