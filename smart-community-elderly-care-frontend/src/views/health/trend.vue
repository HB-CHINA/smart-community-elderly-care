<template>
  <div class="trend-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>健康趋势分析</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="loadTrend"
          />
        </div>
      </template>

      <v-chart class="chart" :option="chartOption" autoresize />

      <el-alert
        title="健康提示"
        type="info"
        :closable="false"
        class="tips"
      >
        <p>• 正常血压范围：收缩压 90-140 mmHg，舒张压 60-90 mmHg</p>
        <p>• 正常心率范围：60-100 次/分钟</p>
        <p>• 如有异常数据，请及时就医或联系社区医生</p>
      </el-alert>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  MarkLineComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

use([
  CanvasRenderer,
  LineChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  MarkLineComponent
])

const userStore = useUserStore()
const dateRange = ref([])
const chartOption = ref({})

const loadTrend = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) return

  const startDate = dateRange.value[0].toISOString().split('T')[0]
  const endDate = dateRange.value[1].toISOString().split('T')[0]

  try {
    const res = await request.get(`/health/elder/${userStore.userInfo.userId}/trend`, {
      params: { startDate, endDate }
    })

    chartOption.value = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['收缩压', '舒张压', '心率'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: res.dates
      },
      yAxis: { type: 'value' },
      series: [
        {
          name: '收缩压',
          type: 'line',
          data: res.systolic,
          markLine: {
            data: [
              { yAxis: 140, name: '上限', lineStyle: { color: '#F56C6C' } },
              { yAxis: 90, name: '下限', lineStyle: { color: '#F56C6C' } }
            ]
          }
        },
        {
          name: '舒张压',
          type: 'line',
          data: res.diastolic,
          markLine: {
            data: [
              { yAxis: 90, name: '上限', lineStyle: { color: '#E6A23C' } },
              { yAxis: 60, name: '下限', lineStyle: { color: '#E6A23C' } }
            ]
          }
        },
        {
          name: '心率',
          type: 'line',
          data: res.heartRate,
          markLine: {
            data: [
              { yAxis: 100, name: '上限', lineStyle: { color: '#67C23A' } }
            ]
          }
        }
      ]
    }
  } catch (error) {
    console.error('加载趋势失败:', error)
  }
}

onMounted(() => {
  // 默认最近30天
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 30)
  dateRange.value = [start, end]
  loadTrend()
})
</script>

<style scoped lang="scss">
.trend-container {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .chart {
    height: 400px;
  }

  .tips {
    margin-top: 20px;

    p {
      margin: 5px 0;
    }
  }
}
</style>
