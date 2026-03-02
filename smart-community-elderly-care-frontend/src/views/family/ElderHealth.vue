<!-- src/views/family/ElderHealth.vue -->
<template>
  <div class="elder-health">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">老人健康监护</span>
        </div>
      </template>

      <!-- 老人选择 -->
      <el-form :inline="true" class="elder-select">
        <el-form-item label="选择老人">
          <el-select v-model="selectedElder" placeholder="请选择老人" @change="handleElderChange">
            <el-option
              v-for="elder in elderList"
              :key="elder.id"
              :label="elder.realName"
              :value="elder"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 健康数据展示 -->
      <div v-if="selectedElder" class="health-display">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6">
            <el-card class="health-card">
              <div class="health-item">
                <div class="health-label">血压</div>
                <div class="health-value">{{ healthData.bloodPressure || '正常' }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-card class="health-card">
              <div class="health-item">
                <div class="health-label">血糖</div>
                <div class="health-value">{{ healthData.bloodSugar || '正常' }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-card class="health-card">
              <div class="health-item">
                <div class="health-label">心率</div>
                <div class="health-value">{{ healthData.heartRate || '正常' }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-card class="health-card">
              <div class="health-item">
                <div class="health-label">体温</div>
                <div class="health-value">{{ healthData.temperature || '正常' }}</div>
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
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { getFamilyElderListApi, getElderHealthApi } from '@/api/health'
import * as echarts from 'echarts'

const elderList = ref([])
const selectedElder = ref(null)
const healthData = ref({})
const trendChart = ref(null)
let chartInstance = null

onMounted(async () => {
  await loadElderList()
})

const loadElderList = async () => {
  try {
    const res = await getFamilyElderListApi()
    elderList.value = res || []
  } catch (error) {
    ElMessage.error(error.message || '加载老人列表失败')
  }
}

const handleElderChange = async (elder) => {
  await loadElderHealth(elder.id)
}

const loadElderHealth = async (elderId) => {
  try {
    const res = await getElderHealthApi(elderId)
    healthData.value = res

    // 初始化图表
    if (chartInstance) {
      chartInstance.dispose()
    }
    chartInstance = echarts.init(trendChart.value)

    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['血压', '血糖', '心率']
      },
      xAxis: {
        type: 'category',
        data: res.dates || []
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '血压',
          type: 'line',
          data: res.bloodPressures || []
        },
        {
          name: '血糖',
          type: 'line',
          data: res.bloodSugars || []
        },
        {
          name: '心率',
          type: 'line',
          data: res.heartRates || []
        }
      ]
    }

    chartInstance.setOption(option)
  } catch (error) {
    console.error('加载健康趋势失败:', error)
  }
}

// 窗口大小改变时重新渲染图表
window.addEventListener('resize', () => {
  if (chartInstance) {
    chartInstance.resize()
  }
})
</script>

<style scoped>
.elder-health {
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

.elder-select {
  margin-bottom: 20px;
}

.health-display {
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
