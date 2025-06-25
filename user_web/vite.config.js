import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// Vite配置
// 作用：配置Vite构建工具的各项参数，包括插件、路径别名、服务器等
export default defineConfig({
  // 插件配置
  plugins: [
    vue() // Vue3支持插件
  ],

  // 路径别名配置
  // 作用：简化导入路径，提高开发效率
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      '@components': resolve(__dirname, 'src/components'),
      '@views': resolve(__dirname, 'src/views'),
      '@stores': resolve(__dirname, 'src/stores'),
      '@utils': resolve(__dirname, 'src/utils')
    }
  },

  // 开发服务器配置
  server: {
    port: 3000,        // 开发服务器端口
    open: true,        // 自动打开浏览器
    cors: true,        // 启用CORS
    proxy: {
      // API代理配置，用于解决开发环境跨域问题
      '/api': {
        target: 'http://localhost:8080/mrsp_server',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },

  // 构建配置
  build: {
    outDir: 'dist',           // 构建输出目录
    assetsDir: 'assets',      // 静态资源目录
    sourcemap: false,         // 是否生成source map
    minify: 'terser',         // 压缩方式
    rollupOptions: {
      output: {
        // 分包策略，优化加载性能
        manualChunks: {
          vue: ['vue', 'vue-router', 'pinia'],
          elementPlus: ['element-plus'],
          utils: ['lodash', 'axios']
        }
      }
    }
  }
})