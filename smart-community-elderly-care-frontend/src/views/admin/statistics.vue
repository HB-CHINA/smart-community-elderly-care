<template>
  <div class="statistics-container">
    <!-- 数据概览 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="item in overviewData" :key="item.title">
        <el-card class="overview-card">
          <div class="overview-item">
            <div class="overview-value">{{ item.value }}</div>
            <div class="overview-title">{{ item.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>近30日签到趋势</span>
          </template>
          <v-chart class="chart" :option="checkinOption" autoresize />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>服务订单统计</span>
          </template>
          <v-chart class="chart" :option="orderOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 报警记录 -->
    <el-card class="alarm-card">
      <template #header>
        <div class="header">
          <span>待处理报警</span>
          <el-button type="primary" @click="loadAlarms">刷新</el-button>
        </div>
      </template>

      <el-table :data="pendingAlarms" stripe>
        <el-table-column prop="alarmType" label="报警类型" />
        <el-table-column prop="elder.name" label="老人姓名" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="triggerTime" label="触发时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.triggerTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleAlarm(row)">
              处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import request from '@/utils/request'
import { formatDate } from '@/utils/format'
import { ElMessage } from 'element-plus'

use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
])

const overviewData = ref([
  { title: '总用户数', value: 0 },
  { title: '老人数量', value: 0 },
  { title: '今日签到', value: 0 },
  { title: '待处理报警', value: 0 }
])

const checkinOption = ref({
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [{
    data: [],
    type: 'line',
    smooth: true,
    areaStyle: {}
  }]
})

const orderOption = ref({
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [{
    data: [],
    type: 'bar'
  }]
})

const pendingAlarms = ref([])

const loadStatistics = async () => {
  try {
    const overview = await request.get('/statistics/overview')
    overviewData.value[0].value = overview.totalUsers
    overviewData.value[1].value = overview.totalElders
    overviewData.value[2].value = overview.todayCheckins
    overviewData.value[3].value = overview.pendingAlarms

    // 加载图表数据
    const endDate = new Date().toISOString().split('T')[0]
    const startDate = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]

    const checkinStats = await request.get('/statistics/daily-checkins', {
      params: { startDate, endDate }
    })
    checkinOption.value.xAxis.data = checkinStats.dates
    checkinOption.value.series[0].data = checkinStats.counts

    const orderStats = await request.get('/statistics/orders', {
      params: { startDate, endDate }
    })
    orderOption.value.xAxis.data = orderStats.dates
    orderOption.value.series[0].data = orderStats.orderCounts
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

const loadAlarms = async () => {
  try {
    const res = await request.get('/alarms/pending')
    pendingAlarms.value = res
  } catch (error) {
    console.error('加载报警失败:', error)
  }
}

const handleAlarm = async (alarm) => {
  try {
    await request.post(`/alarms/${alarm.alarmId}/resolve`, {
      handleNotes: '已处理',
      handlerId: 1 // 当前管理员ID
    })
    ElMessage.success('处理成功')
    loadAlarms()
    loadStatistics()
  } catch (error) {
    console.error('处理失败:', error)
  }
}

onMounted(() => {
  loadStatistics()
  loadAlarms()
})
</script>

<style scoped lang="scss">
.statistics-container {
  .overview-row {
    margin-bottom: 20px;

    .overview-card {
      .overview-item {
        text-align: center;

        .overview-value {
          font-size: 32px;
          font-weight: bold;
          color: #409EFF;
        }

        .overview-title {
          margin-top: 10px;
          color: #909399;
        }
      }
    }
  }

  .chart-row {
    margin-bottom: 20px;

    .chart {
      height: 300px;
    }
  }

  .alarm-card {
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
