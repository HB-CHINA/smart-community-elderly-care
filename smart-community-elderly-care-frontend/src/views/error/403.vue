<template>
  <div class="error-container">
    <el-card class="error-card">
      <h2 class="error-code">403</h2>
      <p class="error-desc">您暂无权限访问该页面</p>
      <el-button type="primary" @click="goBack">返回上一页</el-button>
    </el-card>
  </div>
</template>

<script setup>
const goBack = () => {
  window.history.back()
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
