<!-- src/layout/FamilyLayout.vue -->
<template>
  <div class="family-layout">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-left">
        <el-icon :size="28"><HomeFilled /></el-icon>
        <span class="logo-text">社区智慧养老系统（家属端）</span>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="36" :src="userInfo.avatar || defaultAvatar" />
            <span class="username">{{ userInfo.name }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container style="height: calc(100vh - 60px)">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="aside">
        <el-menu
          default-active="/family/home"
          class="el-menu-vertical-demo"
          router
          text-color="#333"
          active-text-color="#67c23a"
        >
          <el-menu-item index="/family/home">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="/family/elder/health">
            <el-icon><TrendCharts /></el-icon>
            <template #title>老人健康监护</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { HomeFilled, TrendCharts, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo || {})
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 下拉菜单操作
const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/family/profile')
  } else if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出登录成功！')
    router.push('/login')
  }
}
</script>

<style scoped>
.family-layout {
  height: 100vh;
}

.header {
  height: 60px;
  line-height: 60px;
  background-color: #67c23a;
  color: #fff;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
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
  background-color: #fff;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
}

.main {
  padding: 20px;
  background-color: #f5f7fa;
}
</style>
