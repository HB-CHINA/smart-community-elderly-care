<!-- src/layout/AdminLayout.vue -->
<template>
  <div class="admin-layout">
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="logo">
          <el-icon :size="32"><Setting /></el-icon>
          <span class="logo-text">管理后台</span>
        </div>
        <div class="header-right">
          <el-badge :value="pendingAlarms" class="badge-item">
            <el-button :icon="Bell" circle @click="$router.push('/admin/alarm/handle')" />
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="40" :src="userInfo.avatar || defaultAvatar" />
              <span class="username">{{ userInfo.realName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <!-- 侧边菜单 -->
        <el-aside width="220px" class="aside">
          <el-menu
            :default-active="activeMenu"
            router
            class="menu"
          >
            <el-menu-item index="/admin/home">
              <el-icon><HomeFilled /></el-icon>
              <span>数据大屏</span>
            </el-menu-item>
            <el-menu-item index="/admin/user/manage">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/health/monitor">
              <el-icon><TrendCharts /></el-icon>
              <span>健康监控</span>
            </el-menu-item>
            <el-menu-item index="/admin/order/manage">
              <el-icon><List /></el-icon>
              <span>订单管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/alarm/handle">
              <el-icon><Warning /></el-icon>
              <span>报警处理</span>
            </el-menu-item>
            <el-menu-item index="/admin/notice/publish">
              <el-icon><Bell /></el-icon>
              <span>公告发布</span>
            </el-menu-item>
            <el-menu-item index="/admin/stats/export">
              <el-icon><Download /></el-icon>
              <span>数据导出</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 主内容区 -->
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { getStatisticsApi } from '@/api/admin'
import { Bell } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
const activeMenu = computed(() => route.path)
const pendingAlarms = ref(0)

onMounted(async () => {
  await loadPendingAlarms()
})

const loadPendingAlarms = async () => {
  try {
    const res = await getStatisticsApi()
    pendingAlarms.value = res.pendingAlarmCount || 0
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #001529;
  color: white;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  margin-left: 12px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.badge-item {
  line-height: 1;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: white;
}

.username {
  margin: 0 10px;
  font-size: 16px;
}

.aside {
  background: #fff;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
}

.menu {
  border-right: none;
  height: 100%;
}

.main {
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
}
</style>
