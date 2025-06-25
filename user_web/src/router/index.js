import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

// 路由配置数组
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: {
      title: '用户登录',
      requiresAuth: false // 登录页面不需要认证
    }
  },
  {
    path: '/',
    redirect: '/dashboard', // 根路径重定向到仪表板
    component: () => import('../components/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: {
          title: '仪表板',
          icon: 'House'
        }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/Users.vue'),
        meta: {
          title: '用户管理',
          icon: 'User'
        }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/Settings.vue'),
        meta: {
          title: '系统设置',
          icon: 'Setting'
        }
      }
    ]
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
// 作用：在每次路由跳转前进行权限验证和登录状态检查
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 恢复登录状态（防止页面刷新后状态丢失）
  userStore.restoreLoginState()

  // 检查目标路由是否需要认证
  if (to.meta.requiresAuth) {
    if (userStore.isLoggedIn) {
      next() // 已登录，允许访问
    } else {
      next('/login') // 未登录，重定向到登录页
    }
  } else {
    // 如果用户已登录且访问登录页，重定向到首页
    if (to.path === '/login' && userStore.isLoggedIn) {
      next('/dashboard')
    } else {
      next() // 不需要认证的页面，直接访问
    }
  }
})

export default router