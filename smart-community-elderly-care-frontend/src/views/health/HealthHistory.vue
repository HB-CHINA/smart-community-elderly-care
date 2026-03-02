<!-- src/views/health/HealthHistory.vue -->
<template>
  <div class="health-history">
    <el-card>
      <template #header>
        <span class="card-title">健康历史记录</span>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadHealthHistory">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 健康数据列表 -->
      <el-table :data="healthList" v-loading="loading" style="width: 100%">
        <el-table-column prop="createTime" label="记录时间" width="180" />
        <el-table-column prop="systolicPressure" label="收缩压" width="100">
          <template #default="{ row }">
            {{ row.systolicPressure }} mmHg
          </template>
        </el-table-column>
        <el-table-column prop="diastolicPressure" label="舒张压" width="100">
          <template #default="{ row }">
            {{ row.diastolicPressure }} mmHg
          </template>
        </el-table-column>
        <el-table-column prop="bloodSugar" label="血糖" width="100">
          <template #default="{ row }">
            {{ row.bloodSugar }} mmol/L
          </template>
        </el-table-column>
        <el-table-column prop="heartRate" label="心率" width="100">
          <template #default="{ row }">
            {{ row.heartRate }} 次/分
          </template>
        </el-table-column>
        <el-table-column prop="temperature" label="体温" width="100">
          <template #default="{ row }">
            {{ row.temperature }} ℃
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="体重" width="100">
          <template #default="{ row }">
            {{ row.weight }} kg
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewChart(row)">
              查看趋势
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadHealthHistory"
        @current-change="loadHealthHistory"
        class="pagination"
      />
    </el-card>

    <!-- 趋势图对话框 -->
    <el-dialog v-model="chartDialogVisible" title="健康趋势图" width="800px">
      <div ref="trendChart" style="height: 400px;"></div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { getHealthHistoryApi, getHealthTrendApi } from '@/api/health'
import * as echarts from 'echarts'

const userStore = useUserStore()
const loading = ref(false)
const chartDialogVisible = ref(false)
const trendChart = ref(null)
let chartInstance = null

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref([])
const healthList = ref([])

const searchForm = reactive({
  startDate: '',
  endDate: ''
})

onMounted(() => {
  loadHealthHistory()
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})

const loadHealthHistory = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      elderId: userStore.userInfo.id
    }

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    const res = await getHealthHistoryApi(params)
    healthList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error(error.message || '加载健康历史失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  dateRange.value = []
  pageNum.value = 1
  loadHealthHistory()
}

const handleViewChart = async () => {
  chartDialogVisible.value = true
  await nextTick()

  // 初始化图表
  if (chartInstance) {
    chartInstance.dispose()
  }
  chartInstance = echarts.init(trendChart.value)

  try {
    const res = await getHealthTrendApi({
      elderId: userStore.userInfo.id,
      days: 30
    })

    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['收缩压', '舒张压', '血糖', '心率']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: res.dates || []
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '收缩压',
          type: 'line',
          data: res.systolicPressures || []
        },
        {
          name: '舒张压',
          type: 'line',
          data: res.diastolicPressures || []
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
    ElMessage.error(error.message || '加载趋势数据失败')
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
.health-history {
  padding: 20px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
