<template>
  <div class="dashboard-container">
    <!-- 欢迎语 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div>
          <h2>欢迎回来，{{ userStore.userInfo.name }}</h2>
          <p>{{ greeting }}，祝您身体健康，生活愉快！</p>
        </div>
        <el-button
          v-if="userStore.isElder"
          type="danger"
          size="large"
          @click="$router.push('/emergency')"
        >
          <el-icon><Phone /></el-icon>
          紧急求助
        </el-button>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in statistics" :key="stat.title">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-item">
            <div class="stat-icon" :style="{ background: stat.color }">
              <el-icon size="32" color="#fff">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-card class="quick-actions">
      <template #header>
        <span>快捷入口</span>
      </template>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="4" v-for="action in quickActions" :key="action.name">
          <div class="action-item" @click="$router.push(action.path)">
            <el-icon size="40" :color="action.color">
              <component :is="action.icon" />
            </el-icon>
            <span>{{ action.name }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>近7日签到趋势</span>
          </template>
          <v-chart class="chart" :option="checkinOption" autoresize />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>服务分布</span>
          </template>
          <v-chart class="chart" :option="serviceOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新公告 -->
    <el-card class="notice-card">
      <template #header>
        <div class="notice-header">
          <span>最新公告</span>
          <el-link type="primary" @click="$router.push('/notices')">查看更多</el-link>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="notice in latestNotices"
          :key="notice.noticeId"
          :timestamp="formatDate(notice.publishedAt)"
        >
          {{ notice.title }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { formatDate } from '@/utils/format'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
])

const userStore = useUserStore()

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const statistics = ref([
  { title: '今日签到', value: 0, icon: 'Calendar', color: '#409EFF' },
  { title: '我的订单', value: 0, icon: 'Document', color: '#67C23A' },
  { title: '健康记录', value: 0, icon: 'FirstAidKit', color: '#E6A23C' },
  { title: '待处理报警', value: 0, icon: 'Warning', color: '#F56C6C' }
])

const quickActions = [
  { name: '健康打卡', icon: 'Calendar', color: '#409EFF', path: '/health' },
  { name: '预约服务', icon: 'ShoppingBag', color: '#67C23A', path: '/services' },
  { name: '查看订单', icon: 'Document', color: '#E6A23C', path: '/orders' },
  { name: '个人中心', icon: 'User', color: '#909399', path: '/profile' },
  { name: '社区公告', icon: 'Notification', color: '#409EFF', path: '/notices' },
  { name: '紧急求助', icon: 'Phone', color: '#F56C6C', path: '/emergency' }
]

const latestNotices = ref([])

// 签到趋势图表配置
const checkinOption = ref({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: { type: 'value' },
  series: [{
    data: [12, 15, 18, 14, 20, 22, 19],
    type: 'line',
    smooth: true,
    areaStyle: {
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(64,158,255,0.3)' },
          { offset: 1, color: 'rgba(64,158,255,0.05)' }
        ]
      }
    }
  }]
})

// 服务分布图表配置
const serviceOption = ref({
  tooltip: { trigger: 'item' },
  legend: { bottom: '5%' },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    avoidLabelOverlap: false,
    itemStyle: {
      borderRadius: 10,
      borderColor: '#fff',
      borderWidth: 2
    },
    label: { show: false },
    data: [
      { value: 45, name: '家政服务' },
      { value: 30, name: '送餐服务' },
      { value: 25, name: '健康护理' }
    ]
  }]
})

onMounted(async () => {
  // 加载统计数据
  try {
    const overview = await request.get('/statistics/overview')
    statistics.value[0].value = overview.todayCheckins || 0
    statistics.value[1].value = overview.todayOrders || 0
    statistics.value[2].value = overview.totalHealthRecords || 0
    statistics.value[3].value = overview.pendingAlarms || 0

    // 加载公告
    const notices = await request.get('/notices/published')
    latestNotices.value = notices.slice(0, 5)
  } catch (error) {
    console.error('加载数据失败:', error)
  }
})
</script>

<style scoped lang="scss">
.dashboard-container {
  .welcome-card {
    margin-bottom: 20px;

    .welcome-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      h2 {
        margin: 0 0 10px;
        color: #303133;
      }

      p {
        color: #909399;
        margin: 0;
      }
    }
  }

  .stat-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-item {
        display: flex;
        align-items: center;
        gap: 15px;

        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .stat-info {
          .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #303133;
          }

          .stat-title {
            font-size: 14px;
            color: #909399;
            margin-top: 5px;
          }
        }
      }
    }
  }

  .quick-actions {
    margin-bottom: 20px;

    .action-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;
      padding: 20px;
      cursor: pointer;
      border-radius: 8px;
      transition: all 0.3s;

      &:hover {
        background: #f5f7fa;
        transform: translateY(-2px);
      }

      span {
        font-size: 14px;
        color: #606266;
      }
    }
  }

  .chart-row {
    margin-bottom: 20px;

    .chart {
      height: 300px;
    }
  }

  .notice-card {
    .notice-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
