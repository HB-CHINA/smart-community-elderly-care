// vite.config.js 修正版本
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  // 路径别名：适配@符号，避免导入路径错误
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  // 开发环境代理：解决跨域问题，匹配后端接口
  server: {
    host: '0.0.0.0', // 允许局域网访问
    port: 5173, // 前端端口，避免与后端8080冲突
    open: true, // 启动自动打开浏览器
    proxy: {
      // 匹配所有以/api开头的请求
      '/api': {
        target: 'http://localhost:8080', // 你的后端服务地址
        changeOrigin: true, // 开启跨域
        rewrite: (path) => path.replace(/^\/api/, '') // 移除/api前缀（若后端无/api前缀则开启）
        // 若后端接口带/api前缀，注释掉rewrite：rewrite: (path) => path
      }
    }
  },
  // 生产环境打包配置：避免路径错误
  build: {
    outDir: 'dist',
    assetsDir: 'static',
    rollupOptions: {
      output: {
        chunkFileNames: 'static/js/[name]-[hash].js',
        entryFileNames: 'static/js/[name]-[hash].js',
        assetFileNames: 'static/[ext]/[name]-[hash].[ext]'
      }
    }
  }
})
