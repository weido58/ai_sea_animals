import { defineStore } from 'pinia'

export const useTabsStore = defineStore('tabs', {
  state: () => ({
    // 标签页列表，存储所有打开的标签页信息
    tabList: [],
    // 当前激活的标签页路径
    activeTab: ''
  }),

  actions: {
    // 添加新标签页方法
    // 参数：tab - 标签页对象，包含path、name、title等属性
    // 作用：在标签栏中添加新的标签页，避免重复添加
    addTab(tab) {
      // 检查标签页是否已存在
      const exists = this.tabList.some(item => item.path === tab.path)
      if (!exists) {
        this.tabList.push({
          path: tab.path,
          name: tab.name,
          title: tab.title || tab.name,
          closable: tab.path !== '/dashboard' // 首页标签不可关闭
        })
      }
      // 设置为当前激活标签
      this.activeTab = tab.path
    },

    // 移除标签页方法
    // 参数：targetPath - 要移除的标签页路径
    // 作用：从标签栏中移除指定标签页，并处理激活状态切换
    removeTab(targetPath) {
      const index = this.tabList.findIndex(tab => tab.path === targetPath)
      if (index > -1) {
        this.tabList.splice(index, 1)

        // 如果移除的是当前激活标签，需要切换到其他标签
        if (this.activeTab === targetPath && this.tabList.length > 0) {
          // 优先激活前一个标签，如果没有则激活后一个
          const newIndex = index > 0 ? index - 1 : 0
          this.activeTab = this.tabList[newIndex].path
          return this.tabList[newIndex].path
        }
      }
      return null
    },

    // 设置激活标签页方法
    // 参数：path - 标签页路径
    // 作用：切换当前激活的标签页
    setActiveTab(path) {
      this.activeTab = path
    },

    // 关闭其他标签页方法
    // 参数：keepPath - 要保留的标签页路径
    // 作用：关闭除指定标签外的所有其他标签页
    closeOtherTabs(keepPath) {
      this.tabList = this.tabList.filter(tab =>
        tab.path === keepPath || !tab.closable
      )
      this.activeTab = keepPath
    },

    // 关闭所有标签页方法
    // 作用：关闭所有可关闭的标签页，通常保留首页
    closeAllTabs() {
      this.tabList = this.tabList.filter(tab => !tab.closable)
      if (this.tabList.length > 0) {
        this.activeTab = this.tabList[0].path
      }
    }
  }
})