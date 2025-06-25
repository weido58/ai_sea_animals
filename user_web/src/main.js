import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

// 创建Vue应用实例
const app = createApp(App)

// 创建Pinia状态管理实例
const pinia = createPinia()

// 注册Element Plus图标组件
// 作用：全局注册所有Element Plus图标，可在任何组件中使用
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 应用插件和配置
app.use(pinia)           // 使用Pinia状态管理
app.use(router)          // 使用Vue Router路由
app.use(ElementPlus)     // 使用Element Plus UI组件库

// 挂载应用到DOM
app.mount('#app')