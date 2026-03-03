<!-- src/views/family/Home.vue -->
<template>
  <div class="family-home">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">家属监护端</span>
        </div>
      </template>

      <!-- 监护的老人列表 -->
      <el-row :gutter="20" class="elder-list">
        <el-col :xs="24" :sm="12" :md="6" v-for="elder in elderList" :key="elder.userId">
          <el-card class="elder-card" @click="handleSelectElder(elder)">
            <div class="elder-info">
              <el-avatar :size="60" :src="elder.avatar || defaultAvatar" />
              <div class="elder-details">
                <div class="elder-name">{{ elder.name }}</div>
                <div class="elder-phone">{{ formatPhone(elder.phone) }}</div>
                <div class="elder-status">
                  <el-tag type="success">正常</el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 选中老人的健康数据 -->
    <el-card v-if="selectedElder" class="health-data">
      <template #header>
        <div class="card-header">
          <span class="card-title">{{ selectedElder.name }}的健康数据</span>
        </div>
      </template>

      <!-- 健康数据概览 -->
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="health-card">
            <div class="health-item">
              <div class="health-label">血压</div>
              <div class="health-value">{{ healthData.bloodPressure || '--' }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="health-card">
            <div class="health-item">
              <div class="health-label">血糖</div>
              <div class="health-value">{{ healthData.bloodSugar || '--' }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="health-card">
            <div class="health-item">
              <div class="health-label">心率</div>
              <div class="health-value">{{ healthData.heartRate || '--' }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="health-card">
            <div class="health-item">
              <div class="health-label">体温</div>
              <div class="health-value">{{ healthData.temperature || '--' }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 健康趋势图 -->
      <el-card class="trend-chart">
        <template #header>
          <span class="card-title">健康趋势</span>
        </template>
        <div ref="trendChart" style="height: 300px;"></div>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { getFamilyElderListApi, getLatestHealthRecordApi, getHealthTrendApi } from '@/api/health'
import { formatPhone } from '@/utils/format'
import * as echarts from 'echarts'

const elderList = ref([])
const selectedElder = ref(null)
const healthData = ref({})
const trendChart = ref(null)
let chartInstance = null

const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

onMounted(async () => {
  await loadElderList()
})

const loadElderList = async () => {
  try {
    const res = await getFamilyElderListApi()
    elderList.value = res.data || []
  } catch (error) {
    ElMessage.error(error.message || '加载老人列表失败')
  }
}

const handleSelectElder = async (elder) => {
  selectedElder.value = elder
  await loadElderHealth(elder.userId)
}

const loadElderHealth = async (elderId) => {
  try {
    // 获取最新健康数据
    const res = await getLatestHealthRecordApi(elderId)
    healthData.value = res.data || {}

    // 初始化图表
    if (chartInstance) {
      chartInstance.dispose()
    }
    chartInstance = echarts.init(trendChart.value)

    // 获取趋势数据
    const endDate = new Date().toISOString().split('T')[0]
    const startDate = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]

    let trendData = { dates: [], bloodPressures: [], bloodSugars: [], heartRates: [] }
    try {
      const trendRes = await getHealthTrendApi(elderId, startDate, endDate)
      trendData = trendRes.data || trendData
    } catch (e) {
      console.log('获取趋势数据失败，使用默认数据')
    }

    const option = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['血压', '血糖', '心率'] },
      xAxis: {
        type: 'category',
        data: trendData.dates.length > 0 ? trendData.dates : ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: { type: 'value' },
      series: [
        { name: '血压', type: 'line', data: trendData.bloodPressures || [] },
        { name: '血糖', type: 'line', data: trendData.bloodSugars || [] },
        { name: '心率', type: 'line', data: trendData.heartRates || [] }
      ]
    }

    chartInstance.setOption(option)
  } catch (error) {
    console.error('加载健康趋势失败:', error)
  }
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
  }
})
</script>

<style scoped>
.family-home {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
}

.elder-list {
  margin-bottom: 20px;
}

.elder-card {
  cursor: pointer;
  transition: all 0.3s;
}

.elder-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.elder-info {
  display: flex;
  align-items: center;
}

.elder-details {
  margin-left: 15px;
}

.elder-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 5px;
}

.elder-phone {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.health-data {
  margin-top: 20px;
}

.health-card {
  text-align: center;
  padding: 20px;
}

.health-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.health-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.health-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.trend-chart {
  margin-top: 20px;
}
</style>
