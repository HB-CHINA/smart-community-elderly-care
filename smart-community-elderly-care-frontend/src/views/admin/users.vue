<template>
  <div class="admin-users">
    <el-card>
      <template #header>
        <div class="header">
          <span>用户管理</span>
          <el-button type="primary" @click="loadUsers">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : (row.role === 'elder' ? 'primary' : 'success')">
              {{ row.role === 'admin' ? '管理员' : (row.role === 'elder' ? '老人' : '家属') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { formatDate } from '@/utils/format'

const users = ref([])
const loading = ref(false)

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/all')
    users.value = res
  } catch (error) {
    console.error('加载用户失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped lang="scss">
.admin-users {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
