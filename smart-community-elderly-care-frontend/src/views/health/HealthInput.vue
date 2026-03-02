<!-- src/views/health/HealthInput.vue -->
<template>
  <div class="health-input">
    <el-card>
      <template #header>
        <span class="card-title">健康数据录入</span>
      </template>

      <el-form :model="healthForm" :rules="rules" ref="healthFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压" prop="systolicPressure">
              <el-input-number
                v-model="healthForm.systolicPressure"
                :min="60"
                :max="250"
                placeholder="请输入收缩压"
                style="width: 100%"
              />
              <span class="unit">mmHg</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压" prop="diastolicPressure">
              <el-input-number
                v-model="healthForm.diastolicPressure"
                :min="40"
                :max="150"
                placeholder="请输入舒张压"
                style="width: 100%"
              />
              <span class="unit">mmHg</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血糖" prop="bloodSugar">
              <el-input-number
                v-model="healthForm.bloodSugar"
                :min="2"
                :max="30"
                :precision="1"
                placeholder="请输入血糖"
                style="width: 100%"
              />
              <span class="unit">mmol/L</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="心率" prop="heartRate">
              <el-input-number
                v-model="healthForm.heartRate"
                :min="40"
                :max="200"
                placeholder="请输入心率"
                style="width: 100%"
              />
              <span class="unit">次/分</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="体温" prop="temperature">
              <el-input-number
                v-model="healthForm.temperature"
                :min="35"
                :max="42"
                :precision="1"
                placeholder="请输入体温"
                style="width: 100%"
              />
              <span class="unit">℃</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重" prop="weight">
              <el-input-number
                v-model="healthForm.weight"
                :min="30"
                :max="150"
                :precision="1"
                placeholder="请输入体重"
                style="width: 100%"
              />
              <span class="unit">kg</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
            v-model="healthForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="handleSubmit" :loading="loading">
            提交数据
          </el-button>
          <el-button size="large" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 健康提示卡片 -->
    <el-card class="health-tips" v-if="healthTips.length > 0">
      <template #header>
        <span class="card-title">健康提示</span>
      </template>
      <el-alert
        v-for="(tip, index) in healthTips"
        :key="index"
        :title="tip.title"
        :type="tip.type"
        :description="tip.content"
        show-icon
        :closable="false"
        class="tip-item"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { submitHealthDataApi } from '@/api/health'

const userStore = useUserStore()
const healthFormRef = ref(null)
const loading = ref(false)

const healthForm = reactive({
  systolicPressure: null,
  diastolicPressure: null,
  bloodSugar: null,
  heartRate: null,
  temperature: null,
  weight: null,
  remark: ''
})

const rules = {
  systolicPressure: [{ required: true, message: '请输入收缩压', trigger: 'blur' }],
  diastolicPressure: [{ required: true, message: '请输入舒张压', trigger: 'blur' }],
  bloodSugar: [{ required: true, message: '请输入血糖', trigger: 'blur' }],
  heartRate: [{ required: true, message: '请输入心率', trigger: 'blur' }]
}

const healthTips = computed(() => {
  const tips = []

  // 血压判断
  if (healthForm.systolicPressure && healthForm.diastolicPressure) {
    if (healthForm.systolicPressure >= 140 || healthForm.diastolicPressure >= 90) {
      tips.push({
        title: '血压偏高',
        type: 'warning',
        content: '您的血压偏高，建议注意饮食，适当运动，定期监测血压变化。'
      })
    } else if (healthForm.systolicPressure < 90 || healthForm.diastolicPressure < 60) {
      tips.push({
        title: '血压偏低',
        type: 'warning',
        content: '您的血压偏低，建议多补充营养，避免突然站立。'
      })
    }
  }

  // 血糖判断
  if (healthForm.bloodSugar) {
    if (healthForm.bloodSugar > 7.0) {
      tips.push({
        title: '血糖偏高',
        type: 'warning',
        content: '您的空腹血糖偏高，建议控制饮食，减少糖分摄入，必要时咨询医生。'
      })
    } else if (healthForm.bloodSugar < 3.9) {
      tips.push({
        title: '血糖偏低',
        type: 'error',
        content: '您的血糖偏低，建议及时补充糖分，如出现头晕、出汗等症状请立即就医。'
      })
    }
  }

  // 心率判断
  if (healthForm.heartRate) {
    if (healthForm.heartRate > 100 || healthForm.heartRate < 60) {
      tips.push({
        title: '心率异常',
        type: 'warning',
        content: '您的心率不在正常范围内，建议咨询医生。'
      })
    }
  }

  // 体温判断
  if (healthForm.temperature) {
    if (healthForm.temperature > 37.3) {
      tips.push({
        title: '体温偏高',
        type: 'error',
        content: '您的体温偏高，建议多喝水，注意休息，如持续发热请及时就医。'
      })
    }
  }

  return tips
})

const handleSubmit = async () => {
  if (!healthFormRef.value) return

  await healthFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await submitHealthDataApi({
          ...healthForm,
          elderId: userStore.userInfo.id
        })
        ElMessage.success('健康数据提交成功')
        handleReset()
      } catch (error) {
        ElMessage.error(error.message || '提交失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  if (healthFormRef.value) {
    healthFormRef.value.resetFields()
  }
  healthForm.remark = ''
}
</script>

<style scoped>
.health-input {
  padding: 20px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
}

.unit {
  margin-left: 10px;
  color: #666;
  font-size: 14px;
}

.health-tips {
  margin-top: 20px;
}

.tip-item {
  margin-bottom: 10px;
}

.tip-item:last-child {
  margin-bottom: 0;
}
</style>
