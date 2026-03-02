<!-- src/views/notice/NoticeList.vue -->
<template>
  <div class="notice-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">社区公告</span>
          <el-button type="primary" @click="handlePublish" v-if="userInfo.role === 'admin'">
            发布公告
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入公告标题" clearable />
        </el-form-item>
        <el-form-item label="发布时间">
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
          <el-button type="primary" @click="loadNotices">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 公告列表 -->
      <el-table :data="noticeList" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" width="300" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="publisher" label="发布人" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="userInfo.role === 'admin'"
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="userInfo.role === 'admin'"
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
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
        @size-change="loadNotices"
        @current-change="loadNotices"
        class="pagination"
      />
    </el-card>

    <!-- 公告详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="公告详情" width="600px">
      <div class="notice-detail">
        <h3>{{ currentNotice.title }}</h3>
        <p class="publisher-info">
          发布人：{{ currentNotice.publisher }} | 发布时间：{{ currentNotice.publishTime }}
        </p>
        <div class="content" v-html="currentNotice.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { getNoticeListApi, deleteNoticeApi } from '@/api/notice'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const loading = ref(false)
const detailDialogVisible = ref(false)
const currentNotice = ref({})

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref([])
const noticeList = ref([])

const searchForm = reactive({
  title: ''
})

onMounted(() => {
  loadNotices()
})

const loadNotices = async () => {
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

    const res = await getNoticeListApi(params)
    noticeList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error(error.message || '加载公告列表失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.title = ''
  dateRange.value = []
  pageNum.value = 1
  loadNotices()
}

const handlePublish = () => {
  $router.push('/admin/notice/publish')
}

const handleEdit = (row) => {
  $router.push({
    path: '/admin/notice/publish',
    query: { id: row.id }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
      type: 'warning'
    })

    await deleteNoticeApi(row.id)
    ElMessage.success('删除成功')
    loadNotices()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleDetail = (row) => {
  currentNotice.value = row
  detailDialogVisible.value = true
}
</script>

<style scoped>
.notice-list {
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

.notice-detail {
  padding: 20px;
}

.publisher-info {
  margin: 15px 0;
  color: #666;
  font-size: 14px;
}

.content {
  line-height: 1.8;
  color: #333;
}
</style>
