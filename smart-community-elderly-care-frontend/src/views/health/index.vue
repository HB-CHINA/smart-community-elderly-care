<template>
  <div class="health-container">
    <!-- 数据录入卡片 -->
    <el-card class="input-card">
      <template #header>
        <span>今日健康数据录入</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        size="large"
      >
        <el-row :gutter="20">
          <el-col :xs="24" :md="8">
            <el-form-item label="收缩压(mmHg)" prop="systolicPressure">
              <el-input-number
                v-model="form.systolicPressure"
                :min="50"
                :max="250"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="8">
            <el-form-item label="舒张压(mmHg)" prop="diastolicPressure">
              <el-input-number
                v-model="form.diastolicPressure"
                :min="30"
                :max="150"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="8">
            <el-form-item label="心率(次/分)" prop="heartRate">
              <el-input-number
                v-model="form.heartRate"
                :min="30"
                :max="200"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
            v-model="form.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（如：服药情况、身体感受等）"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            保存记录
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 今日记录状态 -->
    <el-card class="today-card">
      <template #header>
        <div class="card-header">
          <span>今日记录</span>
          <el-tag :type="todayRecord ? 'success' : 'warning'">
            {{ todayRecord ? '已打卡' : '未打卡' }}
          </el-tag>
        </div>
      </template>

      <div v-if="todayRecord" class="today-info">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="收缩压">{{ todayRecord.systolicPressure }} mmHg</el-descriptions-item>
          <el-descriptions-item label="舒张压">{{ todayRecord.diastolicPressure }} mmHg</el-descriptions-item>
          <el-descriptions-item label="心率">{{ todayRecord.heartRate }} 次/分</el-descriptions-item>
        </el-descriptions>
        <p v-if="todayRecord.notes" class="notes">备注：{{ todayRecord.notes }}</p>
      </div>

      <el-empty v-else description="今日还未记录健康数据" />
    </el-card>

    <!-- 历史记录 -->
    <el-card class="history-card">
      <template #header>
        <div class="card-header">
          <span>历史记录</span>
          <div>
            <el-button type="primary" text @click="showTrend">
              <el-icon><TrendCharts /></el-icon>
              查看趋势
            </el-button>
            <el-button type="success" text @click="exportExcel">
              <el-icon><Download /></el-icon>
              导出Excel
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="historyRecords" v-loading="loading" stripe>
        <el-table-column prop="recordDate" label="日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.recordDate, 'YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column prop="systolicPressure" label="收缩压" width="100">
          <template #default="{ row }">
            <el-tag :type="getPressureType(row.systolicPressure, 'sys')">
              {{ row.systolicPressure }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="diastolicPressure" label="舒张压" width="100">
          <template #default="{ row }">
            <el-tag :type="getPressureType(row.diastolicPressure, 'dia')">
              {{ row.diastolicPressure }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="heartRate" label="心率" width="100">
          <template #default="{ row }">
            <el-tag :type="getHeartRateType(row.heartRate)">
              {{ row.heartRate }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="isAbnormal(row)" type="danger">异常</el-tag>
            <el-tag v-else type="success">正常</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        class="pagination"
        @current-change="loadHistory"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { formatDate } from '@/utils/format'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const form = ref({
  systolicPressure: 120,
  diastolicPressure: 80,
  heartRate: 75,
  notes: ''
})

const rules = {
  systolicPressure: [{ required: true, message: '请输入收缩压', trigger: 'blur' }],
  diastolicPressure: [{ required: true, message: '请输入舒张压', trigger: 'blur' }],
  heartRate: [{ required: true, message: '请输入心率', trigger: 'blur' }]
}

const submitting = ref(false)
const loading = ref(false)
const todayRecord = ref(null)
const historyRecords = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getPressureType = (value, type) => {
  if (type === 'sys') {
    if (value > 140) return 'danger'
    if (value < 90) return 'warning'
  } else {
    if (value > 90) return 'danger'
    if (value < 60) return 'warning'
  }
  return 'success'
}

const getHeartRateType = (value) => {
  if (value > 100) return 'danger'
  if (value < 60) return 'warning'
  return 'success'
}

const isAbnormal = (record) => {
  return record.systolicPressure > 140 ||
    record.diastolicPressure > 90 ||
    record.heartRate > 100
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await request.post(`/health/${userStore.userInfo.userId}`, form.value)
    ElMessage.success('健康数据保存成功')
    resetForm()
    loadToday()
    loadHistory()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  formRef.value?.resetFields()
  form.value = {
    systolicPressure: 120,
    diastolicPressure: 80,
    heartRate: 75,
    notes: ''
  }
}

const loadToday = async () => {
  try {
    const res = await request.get(`/health/elder/${userStore.userInfo.userId}/today`)
    todayRecord.value = res
  } catch (error) {
    todayRecord.value = null
  }
}

const loadHistory = async () => {
  loading.value = true
  try {
    const res = await request.get(`/health/elder/${userStore.userInfo.userId}`)
    historyRecords.value = res
    total.value = res.length
  } catch (error) {
    console.error('加载历史记录失败:', error)
  } finally {
    loading.value = false
  }
}

const showTrend = () => {
  router.push('/health/trend')
}

const exportExcel = async () => {
  try {
    const response = await request.get(`/health/export?userId=${userStore.userInfo.userId}`, {
      responseType: 'blob'
    })

    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `健康档案_${formatDate(new Date(), 'YYYY-MM-DD')}.xlsx`
    link.click()

    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  loadToday()
  loadHistory()
})
</script>

<style scoped lang="scss">
.health-container {
  .input-card, .today-card, .history-card {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .today-info {
    .notes {
      margin-top: 15px;
      padding: 10px;
      background: #f5f7fa;
      border-radius: 4px;
      color: #606266;
    }
  }

  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
