// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/pinia/userStore'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/user/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/user/Register.vue'),
      meta: { title: '注册' }
    },
    // 老人端路由
    {
      path: '/elder',
      component: () => import('@/layout/ElderLayout.vue'),
      children: [
        {
          path: 'home',
          name: 'elderHome',
          component: () => import('@/views/elder/Home.vue'),
          meta: { title: '老人首页' }
        },
        {
          path: 'health',
          name: 'elderHealth',
          component: () => import('@/views/health/HealthInput.vue'),
          meta: { title: '健康录入' }
        },
        {
          path: 'health/history',
          name: 'elderHealthHistory',
          component: () => import('@/views/health/HealthHistory.vue'),
          meta: { title: '健康历史' }
        },
        {
          path: 'health/trend',
          name: 'elderHealthTrend',
          component: () => import('@/views/health/trend.vue'),
          meta: { title: '健康趋势' }
        },
        {
          path: 'order',
          name: 'elderOrder',
          component: () => import('@/views/order/ServiceList.vue'),
          meta: { title: '服务商城' }
        },
        {
          path: 'order/my',
          name: 'elderMyOrder',
          component: () => import('@/views/order/MyOrder.vue'),
          meta: { title: '我的订单' }
        },
        {
          path: 'notice',
          name: 'elderNotice',
          component: () => import('@/views/notice/NoticeList.vue'),
          meta: { title: '社区公告' }
        },
        {
          path: 'profile',
          name: 'elderProfile',
          component: () => import('@/views/user/Profile.vue'),
          meta: { title: '个人中心' }
        }
      ]
    },
    // 管理员端路由
    {
      path: '/admin',
      component: () => import('@/layout/AdminLayout.vue'),
      children: [
        {
          path: 'home',
          name: 'adminHome',
          component: () => import('@/views/admin/Home.vue'),
          meta: { title: '管理员首页' }
        },
        {
          path: 'user/manage',
          name: 'adminUserManage',
          component: () => import('@/views/admin/UserManage.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'health/monitor',
          name: 'adminHealthMonitor',
          component: () => import('@/views/health/index.vue'),
          meta: { title: '健康监控' }
        },
        {
          path: 'order/manage',
          name: 'adminOrderManage',
          component: () => import('@/views/admin/OrderManage.vue'),
          meta: { title: '订单管理' }
        },
        {
          path: 'alarm/handle',
          name: 'adminAlarmHandle',
          component: () => import('@/views/admin/AlarmHandle.vue'),
          meta: { title: '报警处理' }
        },
        {
          path: 'notice/publish',
          name: 'adminNoticePublish',
          component: () => import('@/views/admin/NoticePublish.vue'),
          meta: { title: '发布公告' }
        },
        {
          path: 'stats/export',
          name: 'adminStatsExport',
          component: () => import('@/views/admin/statistics.vue'),
          meta: { title: '数据导出' }
        }
      ]
    },
    // 家属端路由
    {
      path: '/family',
      component: () => import('@/layout/FamilyLayout.vue'),
      children: [
        {
          path: 'home',
          name: 'familyHome',
          component: () => import('@/views/family/Home.vue'),
          meta: { title: '家属首页' }
        },
        {
          path: 'elder/health',
          name: 'familyElderHealth',
          component: () => import('@/views/family/ElderHealth.vue'),
          meta: { title: '老人健康监护' }
        },
        {
          path: 'profile',
          name: 'familyProfile',
          component: () => import('@/views/user/Profile.vue'),
          meta: { title: '个人中心' }
        }
      ]
    },
    // 错误页面
    {
      path: '/error/401',
      name: 'error401',
      component: () => import('@/views/error/401.vue'),
      meta: { title: '未授权' }
    },
    {
      path: '/error/403',
      name: 'error403',
      component: () => import('@/views/error/403.vue'),
      meta: { title: '无权限' }
    },
    {
      path: '/error/404',
      name: 'error404',
      component: () => import('@/views/error/404.vue'),
      meta: { title: '页面不存在' }
    },
    // 404 兜底
    {
      path: '/:pathMatch(.*)*',
      redirect: '/error/404'
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 设置页面标题
  document.title = to.meta.title || '社区智慧养老系统'

  // 如果是登录或注册页面，直接放行
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }

  // 检查token
  if (!userStore.token) {
    next('/login')
    return
  }

  // 检查角色权限
  const userInfo = userStore.userInfo
  if (userInfo) {
    const role = userInfo.role
    const path = to.path

    // 老人端路由
    if (path.startsWith('/elder') && role !== 'elder') {
      next('/error/403')
      return
    }

    // 管理员端路由
    if (path.startsWith('/admin') && role !== 'admin') {
      next('/error/403')
      return
    }

    // 家属端路由
    if (path.startsWith('/family') && role !== 'family') {
      next('/error/403')
      return
    }
  }

  next()
})

export default router
