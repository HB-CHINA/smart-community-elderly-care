<template>
  <div class="orders-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>我的订单</span>
          <el-radio-group v-model="filterStatus" @change="loadOrders">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="PENDING">待支付</el-radio-button>
            <el-radio-button label="PAID">已支付</el-radio-button>
            <el-radio-button label="COMPLETED">已完成</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="orders" v-loading="loading" stripe>
        <el-table-column prop="orderId" label="订单号" width="180" />
        <el-table-column prop="service.serviceName" label="服务项目" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="scheduledTime" label="预约时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.scheduledTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING'"
              type="primary"
              size="small"
              @click="handlePay(row)"
            >
              去支付
            </el-button>
            <el-button
              v-if="row.status === 'COMPLETED' && !row.rating"
              type="success"
              size="small"
              @click="handleReview(row)"
            >
              评价
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="danger"
              size="small"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 支付对话框 -->
    <el-dialog v-model="payDialogVisible" title="确认支付" width="400px">
      <p>订单号：{{ selectedOrder?.orderId }}</p>
      <p>服务项目：{{ selectedOrder?.service?.serviceName }}</p>
      <p>支付金额：<span class="price">¥{{ selectedOrder?.amount }}</span></p>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="paying" @click="confirmPay">
          确认支付
        </el-button>
      </template>
    </el-dialog>

    <!-- 评价对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="服务评价" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" :max="5" show-score />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.review"
            type="textarea"
            :rows="4"
            placeholder="请输入您的评价"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitReview">
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { formatDate } from '@/utils/format'

const userStore = useUserStore()

const orders = ref([])
const loading = ref(false)
const filterStatus = ref('')

const payDialogVisible = ref(false)
const selectedOrder = ref(null)
const paying = ref(false)

const reviewDialogVisible = ref(false)
const submitting = ref(false)
const reviewForm = ref({
  rating: 5,
  review: ''
})

const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'PAID': 'primary',
    'IN_PROGRESS': 'info',
    'COMPLETED': 'success',
    'CANCELLED': 'info',
    'REVIEWED': 'success'
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'IN_PROGRESS': '服务中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REVIEWED': '已评价'
  }
  return texts[status] || status
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await request.get(`/order/user/${userStore.userInfo.userId}`)
    orders.value = filterStatus.value
      ? res.filter(o => o.status === filterStatus.value)
      : res
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePay = (order) => {
  selectedOrder.value = order
  payDialogVisible.value = true
}

const confirmPay = async () => {
  paying.value = true
  try {
    await request.post('/payment/simulate', {
      orderId: selectedOrder.value.orderId,
      userId: userStore.userInfo.userId
    })
    ElMessage.success('支付成功')
    payDialogVisible.value = false
    loadOrders()
  } catch (error) {
    console.error('支付失败:', error)
  } finally {
    paying.value = false
  }
}

const handleReview = (order) => {
  selectedOrder.value = order
  reviewForm.value = { rating: 5, review: '' }
  reviewDialogVisible.value = true
}

const submitReview = async () => {
  if (!reviewForm.value.review) {
    ElMessage.warning('请输入评价内容')
    return
  }

  submitting.value = true
  try {
    await request.post(`/order/${selectedOrder.value.orderId}/review`, reviewForm.value)
    ElMessage.success('评价成功')
    reviewDialogVisible.value = false
    loadOrders()
  } catch (error) {
    console.error('评价失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await request.put(`/order/${order.orderId}/cancel`)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消失败:', error)
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped lang="scss">
.orders-container {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .price {
    color: #F56C6C;
    font-size: 18px;
    font-weight: bold;
  }
}
</style>
