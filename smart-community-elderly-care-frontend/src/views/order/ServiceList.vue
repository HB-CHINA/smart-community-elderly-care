<!-- src/views/order/ServiceList.vue -->
<template>
  <div class="service-list">
    <el-card>
      <template #header>
        <span class="card-title">服务商城</span>
      </template>

      <!-- 服务分类 -->
      <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="家政服务" name="housekeeping" />
        <el-tab-pane label="医疗陪护" name="medical" />
        <el-tab-pane label="送餐服务" name="meal" />
        <el-tab-pane label="代办服务" name="errand" />
      </el-tabs>

      <!-- 服务列表 -->
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="service in serviceList" :key="service.id">
          <el-card class="service-card" shadow="hover" @click="handleOrder(service)">
            <img :src="service.image || defaultImage" class="service-image" />
            <div class="service-info">
              <div class="service-name">{{ service.name }}</div>
              <div class="service-desc">{{ service.description }}</div>
              <div class="service-price">
                <span class="price">¥{{ service.price }}</span>
                <span class="unit">/次</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadServices"
        class="pagination"
      />
    </el-card>

    <!-- 订单对话框 -->
    <el-dialog v-model="orderDialogVisible" title="预约服务" width="500px">
      <el-form :model="orderForm" :rules="rules" ref="orderFormRef" label-width="100px">
        <el-form-item label="服务名称">
          <el-input :value="currentService.name" disabled />
        </el-form-item>
        <el-form-item label="服务价格">
          <el-input :value="'¥' + currentService.price" disabled />
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker
            v-model="orderForm.appointmentTime"
            type="datetime"
            placeholder="选择预约时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="服务地址" prop="address">
          <el-input v-model="orderForm.address" placeholder="请输入服务地址" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="orderForm.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="orderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitOrder" :loading="loading">确定预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { getServiceListApi, createOrderApi } from '@/api/order'

const userStore = useUserStore()
const loading = ref(false)
const orderDialogVisible = ref(false)
const orderFormRef = ref(null)
const activeCategory = ref('all')
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

const defaultImage = 'https://via.placeholder.com/300x200?text=Service'
const serviceList = ref([])
const currentService = ref({})

const orderForm = reactive({
  appointmentTime: '',
  address: '',
  remark: ''
})

const rules = {
  appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  address: [{ required: true, message: '请输入服务地址', trigger: 'blur' }]
}

onMounted(() => {
  loadServices()
})

const loadServices = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      category: activeCategory.value === 'all' ? '' : activeCategory.value
    }
    const res = await getServiceListApi(params)
    serviceList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error(error.message || '加载服务列表失败')
  }
}

const handleCategoryChange = () => {
  pageNum.value = 1
  loadServices()
}

const handleOrder = (service) => {
  currentService.value = service
  orderForm.address = userStore.userInfo.address || ''
  orderDialogVisible.value = true
}

const handleSubmitOrder = async () => {
  if (!orderFormRef.value) return

  await orderFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await createOrderApi({
          serviceId: currentService.value.id,
          ...orderForm,
          elderId: userStore.userInfo.id
        })
        ElMessage.success('预约成功')
        orderDialogVisible.value = false
      } catch (error) {
        ElMessage.error(error.message || '预约失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.service-list {
  padding: 20px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
}

.service-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.service-card:hover {
  transform: translateY(-5px);
}

.service-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
}

.service-info {
  padding: 10px 0;
}

.service-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.service-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-price {
  display: flex;
  align-items: baseline;
}

.price {
  font-size: 24px;
  color: #ff6b6b;
  font-weight: bold;
}

.unit {
  font-size: 14px;
  color: #999;
  margin-left: 5px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
