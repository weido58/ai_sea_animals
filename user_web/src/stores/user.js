import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  // 状态定义
  state: () => ({
    // 用户信息对象，包含用户名、角色等基本信息
    userInfo: null,
    // 登录状态标识，用于判断用户是否已登录
    isLoggedIn: false,
    // 用户权限列表，用于控制菜单显示和页面访问
    permissions: []
  }),

  // 计算属性
  getters: {
    // 获取用户名的计算属性
    // 作用：提供统一的用户名获取方式，避免重复的null检查
    username: (state) => state.userInfo?.username || '未登录',

    // 检查用户是否具有特定权限
    // 作用：用于组件中的权限判断，控制按钮或功能的显示
    hasPermission: (state) => (permission) => {
      return state.permissions.includes(permission)
    }
  },

  // 动作方法
  actions: {
    // 用户登录方法
    // 参数：loginForm - 包含用户名和密码的登录表单对象
    // 作用：处理用户登录逻辑，验证用户凭据并更新状态
    async login(loginForm) {
      try {
        // 模拟API请求延迟
        await new Promise(resolve => setTimeout(resolve, 1000))

        // 简单的登录验证逻辑（实际项目中应调用后端API）
        if (loginForm.username === 'admin' && loginForm.password === '123456') {
          // 设置用户信息
          this.userInfo = {
            id: 1,
            username: loginForm.username,
            role: 'admin',
            avatar: '/src/assets/avatar.png'
          }
          // 更新登录状态
          this.isLoggedIn = true
          // 设置用户权限
          this.permissions = ['user:read', 'user:write', 'system:config']

          // 将登录状态保存到本地存储
          localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
          localStorage.setItem('isLoggedIn', 'true')

          return { success: true, message: '登录成功' }
        } else {
          return { success: false, message: '用户名或密码错误' }
        }
      } catch (error) {
        return { success: false, message: '登录过程中发生错误' }
      }
    },

    // 用户登出方法
    // 作用：清除用户登录状态和相关信息，返回登录页面
    logout() {
      // 重置状态
      this.userInfo = null
      this.isLoggedIn = false
      this.permissions = []

      // 清除本地存储
      localStorage.removeItem('userInfo')
      localStorage.removeItem('isLoggedIn')
    },

    // 从本地存储恢复登录状态
    // 作用：页面刷新后恢复用户登录状态，提升用户体验
    restoreLoginState() {
      const savedUserInfo = localStorage.getItem('userInfo')
      const savedLoginState = localStorage.getItem('isLoggedIn')

      if (savedUserInfo && savedLoginState === 'true') {
        this.userInfo = JSON.parse(savedUserInfo)
        this.isLoggedIn = true
        this.permissions = ['user:read', 'user:write', 'system:config']
      }
    }
  }
})