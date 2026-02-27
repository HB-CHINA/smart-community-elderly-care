<template>
  <div class="notices-container">
    <el-card v-for="notice in notices" :key="notice.noticeId" class="notice-card">
      <template #header>
        <div class="notice-header">
          <h3>{{ notice.title }}</h3>
          <span class="time">{{ formatDate(notice.publishedAt) }}</span>
        </div>
      </template>
      <div class="notice-content">{{ notice.content }}</div>
      <div class="notice-footer">
        <el-tag size="small" :type="notice.status === 1 ? 'success' : 'info'">
          {{ notice.status === 1 ? '已发布' : '草稿' }}
        </el-tag>
      </div>
    </el-card>

    <el-empty v-if="notices.length === 0" description="暂无公告" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { formatDate } from '@/utils/format'

const notices = ref([])

const loadNotices = async () => {
  try {
    const res = await request.get('/notices/published')
    notices.value = res
  } catch (error) {
    console.error('加载公告失败:', error)
  }
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped lang="scss">
.notices-container {
  .notice-card {
    margin-bottom: 20px;

    .notice-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      h3 {
        margin: 0;
        color: #303133;
      }

      .time {
        color: #909399;
        font-size: 14px;
      }
    }

    .notice-content {
      color: #606266;
      line-height: 1.8;
      white-space: pre-wrap;
    }

    .notice-footer {
      margin-top: 15px;
    }
  }
}
</style>
