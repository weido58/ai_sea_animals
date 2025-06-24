import { createApp } from 'vue'
//import './style.css'
import App from './App.vue'
// 导入路由规则
import router from "./config/router.js";
// 导入 ElementPlus 组件库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 应用路由规则：use(router)
// 应用组件库：use(ElementPlus)
createApp(App).use(router).use(ElementPlus).mount('#app')