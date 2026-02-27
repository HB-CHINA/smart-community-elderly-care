<template>
  <div class="elder-home">
    <el-card class="welcome-card">
      <h2>欢迎您，{{ userStore.userInfo.username }}！</h2>
      <p class="tips">今日健康数据尚未录入，点击下方按钮快速录入～</p>
      <el-button type="primary" @click="goToHealthInput" class="health-btn">
        录入健康数据
      </el-button>
    </el-card>

    <el-row :gutter="20" class="stats-row" style="margin-top: 20px">
      <el-col :span="8">
        <el-card class="stats-card">
          <div class="stats-title">待处理订单</div>
          <div class="stats-value">0</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stats-card">
          <div class="stats-title">未读公告</div>
          <div class="stats-value">0</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stats-card">
          <div class="stats-title">健康异常提醒</div>
          <div class="stats-value">0</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/pinia/userStore'

const router = useRouter()
const userStore = useUserStore()

// 跳转到健康数据录入页
const goToHealthInput = () => {
  router.push('/elder/health')
}
</script>

<style scoped>
.elder-home {
  width: 100%;
}

.welcome-card {
  text-align: center;
  padding: 30px;
}

.welcome-card h2 {
  font-size: 24px;
  margin-bottom: 10px;
}

.tips {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.health-btn {
  width: 200px;
  height: 48px;
  font-size: 18px;
}

.stats-row {
  margin-top: 20px;
}

.stats-card {
  text-align: center;
  padding: 20px;
}

.stats-title {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.stats-value {
  font-size: 30px;
  color: #1989fa;
  font-weight: bold;
}
</style>
