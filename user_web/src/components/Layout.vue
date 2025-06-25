<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <span v-if="!isCollapsed">管理系统</span>
        <span v-else>MS</span>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapsed"
        @select="handleMenuSelect"
      >
        <el-menu-item
          v-for="item in menuItems"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容区域 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <header class="header">
        <div class="header-left">
          <!-- 菜单折叠按钮 -->
          <el-button
            :icon="isCollapsed ? Expand : Fold"
            @click="toggleSidebar"
            circle
          />
        </div>

        <div class="header-right">
          <!-- 用户信息下拉菜单 -->
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
              <span class="username">{{ userStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 标签页栏 -->
      <div class="tabs-container">
        <el-tabs
          v-model="tabsStore.activeTab"
          type="card"
          closable
          @tab-click="handleTabClick"
          @tab-remove="handleTabRemove"
        >
          <el-tab-pane
            v-for="tab in tabsStore.tabList"
            :key="tab.path"
            :label="tab.title"
            :name="tab.path"
            :closable="tab.closable"
          />
        </el-tabs>

        <!-- 标签页操作按钮 -->
        <div class="tabs-actions">
          <el-dropdown @command="handleTabsCommand">
            <el-button size="small" type="primary" link>
              操作 <el-icon><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="closeOthers">关闭其他</el-dropdown-item>
                <el-dropdown-item command="closeAll">关闭所有</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 页面内容区域 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useTabsStore } from '../stores/tabs'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Fold, Expand, ArrowDown } from '@element-plus/icons-vue'

// 获取必要的实例和状态
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const tabsStore = useTabsStore()

// 响应式数据
const isCollapsed = ref(false) // 侧边栏折叠状态

// 菜单项配置
const menuItems = [
  { path: '/dashboard', title: '仪表板', icon: 'House' },
  { path: '/users', title: '用户管理', icon: 'User' },
  { path: '/settings', title: '系统设置', icon: 'Setting' }
]

// 计算当前激活的菜单项
const activeMenu = computed(() => route.path)

// 监听路由变化，自动添加标签页
watch(route, (newRoute) => {
  if (newRoute.path !== '/login') {
    tabsStore.addTab({
      path: newRoute.path,
      name: newRoute.name,
      title: newRoute.meta?.title || newRoute.name
    })
  }
}, { immediate: true })

// 切换侧边栏折叠状态方法
// 作用：控制侧边栏的展开和收起，节省屏幕空间
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

// 菜单选择处理方法
// 参数：path - 选中的菜单路径
// 作用：处理菜单点击事件，实现页面跳转和标签页管理
const handleMenuSelect = (path) => {
  router.push(path)
}

// 标签页点击处理方法
// 参数：tab - 点击的标签页对象
// 作用：处理标签页切换，同步路由跳转
const handleTabClick = (tab) => {
  const targetPath = typeof tab === 'string' ? tab : tab.props.name
  if (route.path !== targetPath) {
    router.push(targetPath)
  }
}

// 标签页移除处理方法
// 参数：targetPath - 要移除的标签页路径
// 作用：处理标签页关闭，必要时切换到其他标签页
const handleTabRemove = (targetPath) => {
  const newActivePath = tabsStore.removeTab(targetPath)
  if (newActivePath && route.path === targetPath) {
    router.push(newActivePath)
  }
}

// 标签页操作命令处理方法
// 参数：command - 操作命令（closeOthers/closeAll）
// 作用：处理批量标签页操作
const handleTabsCommand = (command) => {
  switch (command) {
    case 'closeOthers':
      tabsStore.closeOtherTabs(route.path)
      break
    case 'closeAll':
      tabsStore.closeAllTabs()
      if (tabsStore.tabList.length > 0) {
        router.push(tabsStore.tabList[0].path)
      }
      break
  }
}

// 用户操作命令处理方法
// 参数：command - 用户操作命令
// 作用：处理用户相关操作，如个人中心、退出登录
const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人中心功能开发中...')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        userStore.logout()
        await router.push('/login')
        ElMessage.success('已退出登录')
      } catch (error) {
        // 用户取消操作
      }
      break
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 250px;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.sidebar.collapsed {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  background-color: #409eff;
}

.sidebar-menu {
  border: none;
  background-color: #304156;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background: white;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  margin-left: 8px;
  color: #333;
}

.tabs-container {
  display: flex;
  align-items: center;
  background: white;
  border-bottom: 1px solid #e8e8e8;
  padding: 0 20px;
}

.tabs-container :deep(.el-tabs) {
  flex: 1;
}

.tabs-container :deep(.el-tabs__header) {
  margin: 0;
}

.tabs-actions {
  margin-left: 20px;
}

.content {
  flex: 1;
  padding: 20px;
  background-color: #f0f2f5;
  overflow-y: auto;
}
</style>