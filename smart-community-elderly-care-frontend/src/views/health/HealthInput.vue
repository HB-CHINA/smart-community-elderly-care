<template>
  <div class="health-input-container">
    <el-card title="健康数据录入" class="health-card">
      <el-form
        ref="healthFormRef"
        :model="healthForm"
        :rules="healthRules"
        label-width="100px"
        class="health-form"
      >
        <el-form-item label="收缩压(mmHg)" prop="systolicPressure">
          <el-input-number
            v-model="healthForm.systolicPressure"
            :min="50"
            :max="250"
            placeholder="请输入收缩压"
            class="input-item"
          />
        </el-form-item>
        <el-form-item label="舒张压(mmHg)" prop="diastolicPressure">
          <el-input-number
            v-model="healthForm.diastolicPressure"
            :min="30"
            :max="150"
            placeholder="请输入舒张压"
            class="input-item"
          />
        </el-form-item>
        <el-form-item label="心率(次/分)" prop="heartRate">
          <el-input-number
            v-model="healthForm.heartRate"
            :min="30"
            :max="200"
            placeholder="请输入心率"
            class="input-item"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="healthForm.notes"
            type="textarea"
            rows="3"
            placeholder="请输入备注（如：今日头晕）"
            class="input-item"
          />
        </el-form-item>
        <el-form-item class="submit-btn">
          <el-button type="primary" @click="submitHealthForm">提交记录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { addHealthRecord } from '@/api/health'

const userStore = useUserStore()
const healthFormRef = ref(null)

// 表单数据
const healthForm = reactive({
  systolicPressure: null,
  diastolicPressure: null,
  heartRate: null,
  notes: ''
})

// 表单校验规则（与后端一致：论文5.4.1数据范围校验）
const healthRules = reactive({
  systolicPressure: [
    { required: true, message: '请输入收缩压', trigger: 'blur' },
    { min: 50, max: 250, message: '收缩压范围为50-250mmHg', trigger: 'blur' }
  ],
  diastolicPressure: [
    { required: true, message: '请输入舒张压', trigger: 'blur' },
    { min: 30, max: 150, message: '舒张压范围为30-150mmHg', trigger: 'blur' }
  ],
  heartRate: [
    { required: true, message: '请输入心率', trigger: 'blur' },
    { min: 30, max: 200, message: '心率范围为30-200次/分', trigger: 'blur' }
  ]
})

// 提交健康记录
const submitHealthForm = async () => {
  await healthFormRef.value.validate()
  try {
    await addHealthRecord(userStore.userInfo.userId, healthForm)
    ElMessage.success('健康记录提交成功！')
    // 重置表单
    healthFormRef.value.resetFields()
  } catch (error) {
    ElMessage.error('健康记录提交失败：' + error.message)
  }
}
</script>

<style scoped>
.health-input-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.health-card {
  border-radius: 8px;
}
.health-form {
  margin-top: 20px;
}
.input-item {
  width: 100%;
  font-size: 16px;
}
.submit-btn {
  text-align: center;
  margin-top: 30px;
}
.submit-btn .el-button {
  width: 200px;
  height: 48px;
  font-size: 18px;
}
</style>
