import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    // 配置别名，@指向src
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 8080, // 前端运行端口（与后端8081区分）
    open: true, // 自动打开浏览器
    cors: true // 允许跨域
  }
})
