import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/pinia/userStore'
import { ElMessage } from 'element-plus'

// 公开路由（无需登录）
const publicRoutes = [
  {
    path: '/',
    redirect: '/login' // 首页重定向到登录
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue')
  },
  {
    path: '/401',
    name: '401',
    component: () => import('@/views/error/401.vue')
  },
  {
    path: '/403',
    name: '403',
    component: () => import('@/views/error/403.vue')
  }
]

// 私有路由（需要登录+角色权限）
const privateRoutes = [
  // 老人端核心路由（贴合论文老人角色权限）
  {
    path: '/elder',
    name: 'ElderLayout',
    component: () => import('@/layout/ElderLayout.vue'),
    meta: { role: ['elder'] },
    children: [
      { path: 'home', component: () => import('@/views/elder/Home.vue') }, // 老人首页（SOS悬浮按钮）
      { path: 'health', component: () => import('@/views/health/HealthInput.vue') }, // 健康数据录入
      { path: 'health/history', component: () => import('@/views/health/HealthHistory.vue') }, // 健康历史
      { path: 'order', component: () => import('@/views/order/ServiceList.vue') }, // 服务商城
      { path: 'order/my', component: () => import('@/views/order/MyOrder.vue') }, // 我的订单
      { path: 'notice', component: () => import('@/views/notice/NoticeList.vue') }, // 社区公告
      { path: 'profile', component: () => import('@/views/user/Profile.vue') } // 个人中心（头像/紧急联系人）
    ]
  },
  // 管理员端核心路由（贴合论文管理员角色权限）
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/layout/AdminLayout.vue'),
    meta: { role: ['admin'] },
    children: [
      { path: 'home', component: () => import('@/views/admin/Home.vue') }, // 管理员大屏（数据可视化）
      { path: 'user/manage', component: () => import('@/views/admin/UserManage.vue') }, // 用户管理
      { path: 'health/monitor', component: () => import('@/views/admin/HealthMonitor.vue') }, // 健康监控
      { path: 'order/manage', component: () => import('@/views/admin/OrderManage.vue') }, // 订单管理
      { path: 'alarm/handle', component: () => import('@/views/admin/AlarmHandle.vue') }, // 报警处理
      { path: 'notice/publish', component: () => import('@/views/admin/NoticePublish.vue') }, // 公告发布
      { path: 'stats/export', component: () => import('@/views/stats/StatsExport.vue') } // 数据导出
    ]
  },
  // 家属端路由（贴合论文家属角色权限）
  {
    path: '/family',
    name: 'FamilyLayout',
    component: () => import('@/layout/FamilyLayout.vue'),
    meta: { role: ['family'] },
    children: [
      { path: 'home', component: () => import('@/views/family/Home.vue') }, // 家属首页
      { path: 'elder/health', component: () => import('@/views/family/ElderHealth.vue') }, // 老人健康监护
      { path: 'elder/order', component: () => import('@/views/family/ElderOrder.vue') }, // 老人订单代操作
      { path: 'alarm/notice', component: () => import('@/views/family/AlarmNotice.vue') } // 老人报警提醒
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...publicRoutes, ...privateRoutes]
})

// 路由守卫：鉴权
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  // 未登录：只能访问公开路由
  if (!userStore.token) {
    if (publicRoutes.some(item => item.path === to.path)) {
      next()
    } else {
      ElMessage.warning('请先登录')
      next('/login')
    }
    return
  }

  // 已登录：校验角色权限
  if (to.meta?.role) {
    if (to.meta.role.includes(userStore.role)) {
      next()
    } else {
      ElMessage.error('无权限访问该页面')
      next('/403')
    }
  } else {
    next()
  }
})

export default router
