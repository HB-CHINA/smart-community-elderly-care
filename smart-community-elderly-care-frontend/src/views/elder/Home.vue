<!-- src/views/elder/Home.vue -->
<template>
  <div class="elder-home">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div class="welcome-text">
              <h2>欢迎您，{{ userInfo.name }}</h2>
              <p>今天是 {{ currentDate }}，祝您身体健康</p>
            </div>
            <el-icon class="welcome-icon"><Sunny /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷功能 -->
    <el-row :gutter="20" class="quick-actions">
      <el-col :xs="12" :sm="12" :md="6" v-for="item in quickActions" :key="item.name">
        <el-card class="action-card" @click="handleAction(item.path)">
          <div class="action-content">
            <el-icon class="action-icon" :size="40">
              <component :is="item.icon" />
            </el-icon>
            <div class="action-text">{{ item.name }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 健康提醒 -->
    <el-row :gutter="20" class="health-reminder">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span class="card-title">健康提醒</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(reminder, index) in healthReminders"
              :key="index"
              :timestamp="reminder.time"
              placement="top"
              :type="reminder.type"
            >
              {{ reminder.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新公告 -->
    <el-row :gutter="20" class="notice-section">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">最新公告</span>
              <el-link type="primary" @click="$router.push('/elder/notice')">查看更多</el-link>
            </div>
          </template>
          <el-list>
            <el-list-item v-for="notice in latestNotices" :key="notice.noticeId">
              <el-list-item-meta :title="notice.title" :description="formatDate(notice.publishTime)" />
            </el-list-item>
          </el-list>
        </el-card>
      </el-col>
    </el-row>

    <!-- SOS 悬浮按钮 -->
    <div class="sos-button" @click="handleSOS">
      <el-icon :size="32"><Warning /></el-icon>
      <span>SOS</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { triggerSOSApi } from '@/api/alarm'
import { getNoticeListApi } from '@/api/notice'
import { formatDate } from '@/utils/format'
import {
  Home,
  DocumentAdd,
  Document,
  ShoppingCart,
  List,
  Message,
  User,
  Warning,
  Sunny
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo || {})

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

const quickActions = [
  { name: '健康录入', icon: 'DocumentAdd', path: '/elder/health' },
  { name: '服务商城', icon: 'ShoppingCart', path: '/elder/order' },
  { name: '我的订单', icon: 'List', path: '/elder/order/my' },
  { name: '个人中心', icon: 'User', path: '/elder/profile' }
]

const healthReminders = ref([
  { time: '08:00', content: '该测量血压了', type: 'primary' },
  { time: '12:00', content: '午餐时间，请按时用餐', type: 'success' },
  { time: '15:00', content: '下午运动时间', type: 'warning' }
])

const latestNotices = ref([])

onMounted(async () => {
  await loadNotices()
})

const loadNotices = async () => {
  try {
    const res = await getNoticeListApi()
    latestNotices.value = (res.data || []).slice(0, 5)
  } catch (error) {
    console.error('加载公告失败:', error)
  }
}

const handleAction = (path) => {
  router.push(path)
}

const handleSOS = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要发送紧急求助吗？系统将立即通知社区管理员和您的家属。',
      '紧急求助',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    await triggerSOSApi({
      elderId: userInfo.value.userId,
      alarmType: 'SOS',
      description: '老人触发紧急求助'
    })
    ElMessage.success('紧急求助已发送，请保持电话畅通')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '发送失败')
    }
  }
}
</script>

<style scoped>
.elder-home {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
}

.welcome-text p {
  margin: 0;
  font-size: 18px;
  opacity: 0.9;
}

.welcome-icon {
  font-size: 80px;
  opacity: 0.8;
}

.quick-actions {
  margin-bottom: 20px;
}

.action-card {
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 10px;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-content {
  text-align: center;
  padding: 20px;
}

.action-icon {
  color: #409eff;
  margin-bottom: 10px;
}

.action-text {
  font-size: 18px;
  color: #333;
  font-weight: 500;
}

.health-reminder,
.notice-section {
  margin-bottom: 20px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sos-button {
  position: fixed;
  right: 30px;
  bottom: 80px;
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(238, 90, 111, 0.4);
  transition: all 0.3s;
  z-index: 1000;
}

.sos-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(238, 90, 111, 0.6);
}

.sos-button span {
  font-size: 14px;
  font-weight: bold;
  margin-top: 2px;
}
</style>
