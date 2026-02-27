<template>
  <div class="layout-container" :class="{ 'elderly-mode': userStore.isElderlyMode, 'high-contrast': userStore.isHighContrast }">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <el-icon size="32" color="#fff"><FirstAidKit /></el-icon>
        <span v-show="!isCollapsed">智慧养老</span>
      </div>

      <el-menu
        :default-active="$route.path"
        :collapse="isCollapsed"
        :collapse-transition="false"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-menu-item :index="route.path" v-if="!route.meta?.hidden">
            <el-icon>
              <component :is="route.meta?.icon" />
            </el-icon>
            <template #title>{{ route.meta?.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <header class="navbar">
        <div class="left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
          <breadcrumb />
        </div>

        <div class="right">
          <!-- 适老化切换 -->
          <el-tooltip content="大字体模式">
            <el-button
              :type="userStore.isElderlyMode ? 'primary' : ''"
              circle
              @click="userStore.toggleElderlyMode"
            >
              <el-icon><ZoomIn /></el-icon>
            </el-button>
          </el-tooltip>

          <el-tooltip content="高对比度">
            <el-button
              :type="userStore.isHighContrast ? 'primary' : ''"
              circle
              @click="userStore.toggleHighContrast"
            >
              <el-icon><Sunny /></el-icon>
            </el-button>
          </el-tooltip>

          <!-- SOS按钮 - 仅老人可见 -->
          <el-button
            v-if="userStore.isElder"
            type="danger"
            size="large"
            class="sos-btn"
            @click="handleSOS"
          >
            <el-icon><Phone /></el-icon>
            一键求助
          </el-button>

          <!-- 用户菜单 -->
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="40" :src="userStore.userInfo.avatar || defaultAvatar" />
              <span>{{ userStore.userInfo.name }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import Breadcrumb from './components/Breadcrumb.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapsed = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 过滤有权限的路由
const menuRoutes = computed(() => {
  return route.matched[0].children.filter(r => {
    if (r.meta?.hidden) return false
    if (r.meta?.roles && !r.meta.roles.includes(userStore.userInfo.role)) return false
    return true
  })
})

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
      ElMessage.success('已退出登录')
    })
  }
}

// 一键求助
const handleSOS = async () => {
  try {
    await ElMessageBox.confirm('确定要触发紧急求助吗？', '紧急求助', {
      confirmButtonText: '确定求助',
      cancelButtonText: '取消',
      type: 'danger'
    })

    await request.post(`/emergency/help/${userStore.userInfo.userId}`)
    ElMessage.success('紧急求助已发送，工作人员将尽快联系您')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('求助失败:', error)
    }
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  display: flex;
  height: 100vh;

  .sidebar {
    width: 210px;
    background-color: #304156;
    transition: width 0.3s;

    &.collapsed {
      width: 64px;
    }

    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      color: #fff;
      font-size: 20px;
      font-weight: bold;
      border-bottom: 1px solid #1f2d3d;
    }

    .el-menu {
      border-right: none;
    }
  }

  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .navbar {
      height: 60px;
      background: #fff;
      box-shadow: 0 1px 4px rgba(0,21,41,.08);
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;

      .left {
        display: flex;
        align-items: center;
        gap: 15px;

        .collapse-btn {
          font-size: 20px;
          cursor: pointer;
          color: #606266;
        }
      }

      .right {
        display: flex;
        align-items: center;
        gap: 15px;

        .sos-btn {
          font-size: 16px;
          font-weight: bold;
          animation: pulse 2s infinite;
        }

        @keyframes pulse {
          0%, 100% { transform: scale(1); }
          50% { transform: scale(1.05); }
        }

        .user-info {
          display: flex;
          align-items: center;
          gap: 8px;
          cursor: pointer;
          color: #606266;
        }
      }
    }

    .app-main {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
      background-color: #f0f2f5;
    }
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
