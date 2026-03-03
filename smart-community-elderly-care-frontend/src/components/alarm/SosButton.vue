<template>
  <div class="sos-btn-container">
    <el-button
      type="danger"
      round
      icon="Warning"
      size="default"
      class="sos-btn"
      @click="showSosConfirm"
    >
      紧急求助
    </el-button>

    <!-- 二次确认弹窗 -->
    <el-dialog
      title="紧急求助确认"
      v-model="showConfirm"
      width="300px"
    >
      <p class="sos-tip">点击确认后，系统将立即通知社区管理员和您的家属！</p>
      <template #footer>
        <el-button @click="showConfirm = false">取消</el-button>
        <el-button type="danger" @click="handleSos">确认求助</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { triggerSOSApi } from '@/api/alarm'

const showConfirm = ref(false)
const userStore = useUserStore()

// 显示确认弹窗
const showSosConfirm = () => {
  showConfirm.value = true
}

// 触发SOS求助
const handleSos = async () => {
  try {
    const userInfo = userStore.userInfo
    await triggerSOSApi({
      elderId: userInfo.userId,
      alarmType: 'SOS',
      description: '老人触发紧急求助'
    })
    ElMessage.success('求助信号已发送，管理员将尽快响应！')
    showConfirm.value = false
  } catch (error) {
    ElMessage.error('求助信号发送失败，请重试！')
  }
}
</script>

<style scoped>
/* 适老化大按钮：固定悬浮，红色醒目 */
.sos-btn-container {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}

.sos-btn {
  width: 120px;
  height: 48px;
  font-size: 16px;
  background-color: #f56c6c;
  border: none;
}

.sos-tip {
  font-size: 16px;
  color: #f56c6c;
  text-align: center;
  margin-top: 10px;
}
</style>
