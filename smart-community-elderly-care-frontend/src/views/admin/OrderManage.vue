<!-- src/views/admin/OrderManage.vue -->
<template>
  <div class="order-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">订单管理</span>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单编号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号" clearable />
        </el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="searchForm.serviceType" placeholder="请选择服务类型" clearable>
            <el-option label="家政服务" value="housekeeping" />
            <el-option label="医疗陪护" value="medical" />
            <el-option label="送餐服务" value="meal" />
            <el-option label="代办服务" value="errand" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择订单状态" clearable>
            <el-option label="待接单" value="pending" />
            <el-option label="进行中" value="processing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
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
          <el-button type="primary" @click="loadOrders">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 订单列表 -->
      <el-table :data="orderList" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="serviceName" label="服务名称" width="150" />
        <el-table-column prop="serviceType" label="服务类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getServiceType(row.serviceType)">
              {{ getServiceTypeName(row.serviceType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="elderName" label="下单老人" width="120" />
        <el-table-column prop="price" label="订单金额" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" width="180" />
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              size="small"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadOrders"
        @current-change="loadOrders"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminOrderListApi, cancelOrderApi } from '@/api/admin'

const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  orderNo: '',
  serviceType: '',
  status: ''
})

const dateRange = ref([])
const orderList = ref([])

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    }

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    const res = await getAdminOrderListApi(params)
    orderList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error(error.message || '加载订单列表失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.orderNo = ''
  searchForm.serviceType = ''
  searchForm.status = ''
  dateRange.value = []
  pageNum.value = 1
  loadOrders()
}

const handleDetail = (row) => {
  // 跳转到订单详情页面
  $router.push({
    path: '/admin/order/detail',
    query: { id: row.id }
  })
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      type: 'warning'
    })

    await cancelOrderApi(row.id)
    ElMessage.success('取消成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消失败')
    }
  }
}

const getServiceType = (type) => {
  const typeMap = {
    housekeeping: 'primary',
    medical: 'success',
    meal: 'warning',
    errand: 'info'
  }
  return typeMap[type] || 'info'
}

const getServiceTypeName = (type) => {
  const nameMap = {
    housekeeping: '家政服务',
    medical: '医疗陪护',
    meal: '送餐服务',
    errand: '代办服务'
  }
  return nameMap[type] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusName = (status) => {
  const nameMap = {
    pending: '待接单',
    processing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return nameMap[status] || '未知'
}
</script>

<style scoped>
.order-manage {
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

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
