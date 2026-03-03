<!-- src/views/admin/Home.vue -->
<template>
  <div class="admin-home">
    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in statsData" :key="stat.title">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
            <el-icon class="stat-icon" :size="50" :style="{ color: stat.color }">
              <component :is="stat.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span class="card-title">健康数据趋势</span>
          </template>
          <div ref="healthChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span class="card-title">服务订单统计</span>
          </template>
          <div ref="orderChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近报警 -->
    <el-row :gutter="20" class="alarm-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">最近报警</span>
              <el-link type="primary" @click="$router.push('/admin/alarm/handle')">查看全部</el-link>
            </div>
          </template>
          <el-table :data="recentAlarms" style="width: 100%" v-loading="loading">
            <el-table-column prop="elderName" label="报警老人" width="120" />
            <el-table-column prop="alarmType" label="报警类型" width="120">
              <template #default="{ row }">
                <el-tag :type="row.alarmType === 'SOS' ? 'danger' : 'warning'">
                  {{ row.alarmType }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="报警内容" />
            <el-table-column prop="alarmTime" label="报警时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.alarmTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '待处理' ? 'danger' : 'success'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Warning, Document, TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getStatisticsApi, getRecentAlarmsApi } from '@/api/admin'
import { formatDate } from '@/utils/format'

const router = useRouter()
const loading = ref(false)

// 统计数据
const statsData = reactive([
  { title: '注册老人', value: 0, icon: 'User', color: '#409eff' },
  { title: '待处理报警', value: 0, icon: 'Warning', color: '#f56c6c' },
  { title: '今日订单', value: 0, icon: 'Document', color: '#67c23a' },
  { title: '健康监测', value: 0, icon: 'TrendCharts', color: '#e6a23c' }
])

// 最近报警数据
const recentAlarms = ref([])

// 图表实例
const healthChart = ref(null)
const orderChart = ref(null)
let healthChartInstance = null
let orderChartInstance = null

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getStatisticsApi()
    if (res.data) {
      statsData[0].value = res.data.elderCount || 0
      statsData[1].value = res.data.pendingAlarmCount || 0
      statsData[2].value = res.data.todayOrderCount || 0
      statsData[3].value = res.data.healthMonitorCount || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载最近报警
const loadRecentAlarms = async () => {
  loading.value = true
  try {
    const res = await getRecentAlarmsApi({ pageNum: 1, pageSize: 5 })
    recentAlarms.value = res.data || []
  } catch (error) {
    console.error('加载报警数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化健康趋势图表
const initHealthChart = () => {
  if (healthChart.value) {
    healthChartInstance = echarts.init(healthChart.value)
    const option = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['血压', '血糖', '心率'] },
      xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: { type: 'value' },
      series: [
        { name: '血压', type: 'line', data: [120, 118, 125, 122, 119, 121, 117] },
        { name: '血糖', type: 'line', data: [5.2, 5.5, 5.1, 5.8, 5.3, 5.0, 5.4] },
        { name: '心率', type: 'line', data: [72, 75, 70, 78, 73, 71, 76] }
      ]
    }
    healthChartInstance.setOption(option)
  }
}

// 初始化订单统计图表
const initOrderChart = () => {
  if (orderChart.value) {
    orderChartInstance = echarts.init(orderChart.value)
    const option = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['订单数量'] },
      xAxis: {
        type: 'category',
        data: ['家政服务', '健康护理', '餐饮配送', '陪同就医', '其他']
      },
      yAxis: { type: 'value' },
      series: [
        {
          name: '订单数量',
          type: 'bar',
          data: [45, 38, 62, 25, 18],
          itemStyle: { color: '#409eff' }
        }
      ]
    }
    orderChartInstance.setOption(option)
  }
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  healthChartInstance?.resize()
  orderChartInstance?.resize()
}

onMounted(async () => {
  await Promise.all([loadStatistics(), loadRecentAlarms()])
  initHealthChart()
  initOrderChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  healthChartInstance?.dispose()
  orderChartInstance?.dispose()
})
</script>

<style scoped>
.admin-home {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.stat-icon {
  opacity: 0.8;
}

.charts-row,
.alarm-row {
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
