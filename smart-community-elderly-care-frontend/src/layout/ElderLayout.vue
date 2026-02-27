<template>
  <div class="elder-layout">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-left">社区智慧养老系统（老人端）</div>
      <div class="header-right">
        <el-button type="text" @click="showSetting">适老化设置</el-button>
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            {{ userStore.userInfo.username }} <el-icon class="el-icon--right"><arrow-down /></el-icon>
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
          default-active="/elder/home"
          class="el-menu-vertical-demo"
          router
          text-color="#333"
          active-text-color="#1989fa"
        >
          <el-menu-item index="/elder/home">
            <el-icon><home /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="/elder/health">
            <el-icon><document-add /></el-icon>
            <template #title>健康数据录入</template>
          </el-menu-item>
          <el-menu-item index="/elder/health/history">
            <el-icon><document /></el-icon>
            <template #title>健康历史查询</template>
          </el-menu-item>
          <el-menu-item index="/elder/order">
            <el-icon><shopping-cart /></el-icon>
            <template #title>服务商城</template>
          </el-menu-item>
          <el-menu-item index="/elder/order/my">
            <el-icon><list /></el-icon>
            <template #title>我的订单</template>
          </el-menu-item>
          <el-menu-item index="/elder/notice">
            <el-icon><message /></el-icon>
            <template #title>社区公告</template>
          </el-menu-item>
          <el-menu-item index="/elder/profile">
            <el-icon><user /></el-icon>
            <template #title>个人中心</template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <!-- 主内容区 -->
      <el-main class="main">
        <router-view />
        <!-- SOS悬浮按钮 -->
        <SosButton />
      </el-main>
    </el-container>

    <!-- 适老化设置弹窗 -->
    <el-dialog title="适老化设置" v-model="settingVisible" width="300px">
      <el-form label-width="100px">
        <el-form-item label="字体大小">
          <el-radio-group v-model="fontSize" @change="changeFontSize">
            <el-radio label="14px">默认</el-radio>
            <el-radio label="16px">大号</el-radio>
            <el-radio label="18px">超大号</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="高对比度">
          <el-switch v-model="highContrast" @change="toggleHighContrast" />
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/pinia/userStore'
import { useSettingStore } from '@/pinia/settingStore'
import SosButton from '@/components/alarm/SosButton.vue'
import {
  Home,
  DocumentAdd,
  Document,
  ShoppingCart,
  List,
  Message,
  User,
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const settingStore = useSettingStore()

// 适老化设置弹窗
const settingVisible = ref(false)
const fontSize = ref(settingStore.fontSize)
const highContrast = ref(settingStore.highContrast)

// 显示设置弹窗
const showSetting = () => {
  settingVisible.value = true
}

// 切换字体大小
const changeFontSize = (size) => {
  settingStore.changeFontSize(size)
}

// 切换高对比度
const toggleHighContrast = (status) => {
  settingStore.toggleHighContrast(status)
}

// 下拉菜单操作
const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/elder/profile')
  } else if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出登录成功！')
    router.push('/login')
  }
}
</script>

<style scoped>
.elder-layout {
  height: 100vh;
}

.header {
  height: 60px;
  line-height: 60px;
  background-color: #1989fa;
  color: #fff;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
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
