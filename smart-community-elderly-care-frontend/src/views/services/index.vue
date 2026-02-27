<template>
  <div class="services-container">
    <!-- 分类筛选 -->
    <el-card class="filter-card">
      <el-radio-group v-model="selectedCategory" @change="filterServices">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="家政">家政服务</el-radio-button>
        <el-radio-button label="送餐">送餐服务</el-radio-button>
        <el-radio-button label="护理">健康护理</el-radio-button>
        <el-radio-button label="陪伴">陪伴服务</el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- 服务列表 -->
    <el-row :gutter="20" class="service-list">
      <el-col
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
        v-for="service in filteredServices"
        :key="service.serviceId"
      >
        <el-card class="service-card" :body-style="{ padding: '0px' }">
          <div class="service-image">
            <el-icon size="64" color="#909399"><Service /></el-icon>
          </div>

          <div class="service-info">
            <h3>{{ service.serviceName }}</h3>
            <el-tag size="small" :type="getCategoryType(service.category)">
              {{ service.category }}
            </el-tag>
            <p class="description">{{ service.description }}</p>
            <div class="service-footer">
              <span class="price">¥{{ service.price }}</span>
              <el-button type="primary" @click="handleOrder(service)">
                立即预约
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预约对话框 -->
    <el-dialog v-model="orderDialogVisible" title="预约服务" width="500px">
      <el-form :model="orderForm" label-width="100px">
        <el-form-item label="服务项目">
          <span>{{ selectedService?.serviceName }}</span>
        </el-form-item>
        <el-form-item label="服务价格">
          <span class="price">¥{{ selectedService?.price }}</span>
        </el-form-item>
        <el-form-item label="预约时间" required>
          <el-date-picker
            v-model="orderForm.scheduledTime"
            type="datetime"
            placeholder="选择服务时间"
            :disabled-date="disabledDate"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="orderDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">
          确认预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const services = ref([])
const selectedCategory = ref('')
const orderDialogVisible = ref(false)
const selectedService = ref(null)
const submitting = ref(false)

const orderForm = ref({
  scheduledTime: ''
})

const filteredServices = computed(() => {
  if (!selectedCategory.value) return services.value
  return services.value.filter(s => s.category === selectedCategory.value)
})

const getCategoryType = (category) => {
  const types = { '家政': 'primary', '送餐': 'success', '护理': 'warning', '陪伴': 'info' }
  return types[category] || ''
}

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const loadServices = async () => {
  try {
    const res = await request.get('/service/available')
    services.value = res
  } catch (error) {
    console.error('加载服务失败:', error)
  }
}

const filterServices = () => {
  // 筛选逻辑在computed中处理
}

const handleOrder = (service) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  selectedService.value = service
  orderForm.value.scheduledTime = ''
  orderDialogVisible.value = true
}

const submitOrder = async () => {
  if (!orderForm.value.scheduledTime) {
    ElMessage.warning('请选择预约时间')
    return
  }

  submitting.value = true
  try {
    await request.post('/order', {
      userId: userStore.userInfo.userId,
      serviceId: selectedService.value.serviceId,
      scheduledTime: orderForm.value.scheduledTime.toLocaleString()
    })
    ElMessage.success('预约成功')
    orderDialogVisible.value = false
    router.push('/orders')
  } catch (error) {
    console.error('预约失败:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadServices()
})
</script>

<style scoped lang="scss">
.services-container {
  .filter-card {
    margin-bottom: 20px;
  }

  .service-list {
    .service-card {
      margin-bottom: 20px;
      transition: transform 0.3s;

      &:hover {
        transform: translateY(-5px);
      }

      .service-image {
        height: 160px;
        background: #f5f7fa;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .service-info {
        padding: 15px;

        h3 {
          margin: 0 0 10px;
          font-size: 18px;
        }

        .description {
          color: #909399;
          font-size: 14px;
          margin: 10px 0;
          height: 40px;
          overflow: hidden;
        }

        .service-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 15px;

          .price {
            color: #F56C6C;
            font-size: 20px;
            font-weight: bold;
          }
        }
      }
    }
  }
}
</style>
