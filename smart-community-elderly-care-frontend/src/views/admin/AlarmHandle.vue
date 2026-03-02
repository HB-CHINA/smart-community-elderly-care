<!-- src/views/admin/AlarmHandle.vue -->
<template>
  <div class="alarm-handle">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">报警处理</span>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="报警类型">
          <el-select v-model="searchForm.type" placeholder="请选择报警类型" clearable>
            <el-option label="SOS" value="SOS" />
            <el-option label="健康异常" value="health" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="searchForm.status" placeholder="请选择处理状态" clearable>
            <el-option label="未处理" value="pending" />
            <el-option label="已处理" value="handled" />
          </el-select>
        </el-form-item>
        <el-form-item label="报警时间">
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
          <el-button type="primary" @click="loadAlarms">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 报警列表 -->
      <el-table :data="alarmList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="elderName" label="报警老人" width="120" />
        <el-table-column prop="type" label="报警类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'SOS' ? 'danger' : 'warning'">
              {{ row.type === 'SOS' ? 'SOS' : '健康异常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="报警内容" />
        <el-table-column prop="time" label="报警时间" width="180" />
        <el-table-column prop="status" label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'handled' ? 'success' : 'danger'">
              {{ row.status === 'handled' ? '已处理' : '未处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="primary"
              size="small"
              @click="handleHandle(row)"
            >
              处理
            </el-button>
            <el-button
              v-if="row.status === 'handled'"
              type="info"
              size="small"
              @click="handleDetail(row)"
            >
              详情
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
        @size-change="loadAlarms"
        @current-change="loadAlarms"
        class="pagination"
      />
    </el-card>

    <!-- 处理对话框 -->
    <el-dialog v-model="handleDialogVisible" title="处理报警" width="500px">
      <el-form :model="handleForm" ref="handleFormRef" label-width="100px">
        <el-form-item label="处理备注">
          <el-input
            v-model="handleForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入处理备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import { getAlarmListApi, handleAlarmApi } from '@/api/alarm'

const route = useRoute()
const loading = ref(false)
const submitLoading = ref(false)
const handleDialogVisible = ref(false)
const handleFormRef = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  type: '',
  status: ''
})

const dateRange = ref([])
const alarmList = ref([])

const handleForm = reactive({
  remark: ''
})

const currentAlarmId = ref(null)

onMounted(() => {
  loadAlarms()
})

const loadAlarms = async () => {
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

    const res = await getAlarmListApi(params)
    alarmList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error(error.message || '加载报警列表失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.type = ''
  searchForm.status = ''
  dateRange.value = []
  pageNum.value = 1
  loadAlarms()
}

const handleHandle = (row) => {
  currentAlarmId.value = row.id
  handleForm.remark = ''
  handleDialogVisible.value = true
}

const handleSubmit = async () => {
  if (!handleFormRef.value) return

  await handleFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await handleAlarmApi({
          id: currentAlarmId.value,
          ...handleForm
        })
        ElMessage.success('处理成功')
        handleDialogVisible.value = false
        loadAlarms()
      } catch (error) {
        ElMessage.error(error.message || '处理失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDetail = (row) => {
  // 跳转到详情页面
  $router.push({
    path: '/admin/alarm/detail',
    query: { id: row.id }
  })
}
</script>

<style scoped>
.alarm-handle {
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
