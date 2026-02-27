<template>
  <div class="emergency-container">
    <el-card class="sos-card">
      <div class="sos-content">
        <el-icon size="120" color="#F56C6C"><WarningFilled /></el-icon>
        <h1>紧急求助</h1>
        <p>如遇紧急情况，请点击下方按钮</p>
        <p class="sub-text">系统将立即通知社区工作人员和您的紧急联系人</p>

        <el-button
          type="danger"
          size="large"
          class="sos-button"
          :loading="sending"
          @click="handleEmergency"
        >
          <el-icon size="32"><Phone /></el-icon>
          <span>一键求助</span>
        </el-button>
      </div>
    </el-card>

    <el-card class="history-card">
      <template #header>
        <span>求助记录</span>
      </template>

      <el-timeline>
        <el-timeline-item
          v-for="record in emergencyRecords"
          :key="record.alarmId"
          :type="record.status === '待处理' ? 'danger' : 'success'"
          :timestamp="formatDate(record.triggerTime)"
        >
          <h4>{{ record.alarmType }}</h4>
          <p>{{ record.description }}</p>
          <el-tag :type="record.status === '待处理' ? 'danger' : 'success'">
            {{ record.status }}
          </el-tag>
          <p v-if="record.handleNotes" class="handle-notes">
            处理备注：{{ record.handleNotes }}
          </p>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-if="emergencyRecords.length === 0" description="暂无求助记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { formatDate } from '@/utils/format'

const userStore = useUserStore()

const sending = ref(false)
const emergencyRecords = ref([])

const handleEmergency = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要触发紧急求助吗？这将通知社区工作人员。',
      '紧急求助确认',
      {
        confirmButtonText: '确定求助',
        cancelButtonText: '取消',
        type: 'danger'
      }
    )

    sending.value = true
    await request.post(`/emergency/help/${userStore.userInfo.userId}`)
    ElMessage.success('紧急求助已发送，工作人员将尽快联系您')
    loadRecords()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('求助失败:', error)
    }
  } finally {
    sending.value = false
  }
}

const loadRecords = async () => {
  try {
    const res = await request.get(`/emergency/records/${userStore.userInfo.userId}`)
    emergencyRecords.value = res
  } catch (error) {
    console.error('加载记录失败:', error)
  }
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped lang="scss">
.emergency-container {
  max-width: 800px;
  margin: 0 auto;

  .sos-card {
    margin-bottom: 20px;
    text-align: center;

    .sos-content {
      padding: 40px;

      h1 {
        font-size: 32px;
        color: #F56C6C;
        margin: 20px 0;
      }

      p {
        font-size: 18px;
        color: #606266;
        margin: 10px 0;

        &.sub-text {
          font-size: 14px;
          color: #909399;
        }
      }

      .sos-button {
        margin-top: 30px;
        width: 200px;
        height: 80px;
        font-size: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
        animation: pulse 2s infinite;
      }

      @keyframes pulse {
        0%, 100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(245, 108, 108, 0.7); }
        50% { transform: scale(1.05); box-shadow: 0 0 0 20px rgba(245, 108, 108, 0); }
      }
    }
  }

  .history-card {
    .handle-notes {
      margin-top: 10px;
      padding: 10px;
      background: #f5f7fa;
      border-radius: 4px;
      color: #606266;
    }
  }
}
</style>
