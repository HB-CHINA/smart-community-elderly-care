import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
// Element Plus 引入（全局+按需，适配适老化）
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 适老化基础样式
import './assets/css/base.css'
import './assets/css/age-friendly.css'
// 全局工具
import * as ElIcons from '@element-plus/icons-vue'
import request from './utils/request'

const app = createApp(App)
const pinia = createPinia()

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElIcons)) {
  app.component(key, component)
}

// 全局挂载axios请求
app.config.globalProperties.$http = request

app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  // 适老化默认配置：大字体
  size: 'large'
})
app.mount('#app')
