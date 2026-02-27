import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      // 仪表盘
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      // 健康档案
      {
        path: 'health',
        name: 'Health',
        component: () => import('@/views/health/index.vue'),
        meta: { title: '健康档案', icon: 'FirstAidKit', roles: ['elder', 'admin'] }
      },
      {
        path: 'health/trend',
        name: 'HealthTrend',
        component: () => import('@/views/health/trend.vue'),
        meta: { title: '健康趋势', icon: 'TrendCharts', roles: ['elder', 'admin'] }
      },
      // 服务商城
      {
        path: 'services',
        name: 'Services',
        component: () => import('@/views/services/index.vue'),
        meta: { title: '服务商城', icon: 'ShoppingBag' }
      },
      // 我的订单
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/orders/index.vue'),
        meta: { title: '我的订单', icon: 'Document', roles: ['elder', 'admin'] }
      },
      // 紧急求助
      {
        path: 'emergency',
        name: 'Emergency',
        component: () => import('@/views/emergency/index.vue'),
        meta: { title: '紧急求助', icon: 'WarningFilled', roles: ['elder'] }
      },
      // 报警记录（管理员）
      {
        path: 'alarms',
        name: 'Alarms',
        component: () => import('@/views/alarms/index.vue'),
        meta: { title: '报警记录', icon: 'BellFilled', roles: ['admin'] }
      },
      // 社区公告
      {
        path: 'notices',
        name: 'Notices',
        component: () => import('@/views/notices/index.vue'),
        meta: { title: '社区公告', icon: 'Notification' }
      },
      // 个人中心
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'UserFilled' }
      },
      // 管理员功能
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/users.vue'),
        meta: { title: '用户管理', icon: 'User', roles: ['admin'] }
      },
      {
        path: 'admin/statistics',
        name: 'AdminStatistics',
        component: () => import('@/views/admin/statistics.vue'),
        meta: { title: '数据统计', icon: 'DataLine', roles: ['admin'] }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (!to.meta.public && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.roles && !to.meta.roles.includes(userStore.userInfo.role)) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router

