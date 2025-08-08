import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

// 样式导入
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/assets/styles/variables.css'
import '@/assets/styles/main.css'
import '@/assets/styles/components.css'

import App from './App.vue'
import router from './router'
import { envUtil } from '@/utils/env.ts'

// 创建应用实例
const app = createApp(App)

// 创建 Pinia 实例
const pinia = createPinia()

// 注册 Pinia 状态管理
app.use(pinia)

// 注册路由
app.use(router)

// 注册 Element Plus
app.use(ElementPlus, {
  locale: zhCn,
  // 全局配置
  size: 'default',
  zIndex: 3000,
})

// 注册 Element Plus 所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('Global error:', err)
  console.error('Component:', vm)
  console.error('Error info:', info)
}

// 全局警告处理
app.config.warnHandler = (msg, vm, trace) => {
  console.warn('Global warning:', msg)
  console.warn('Component:', vm)
  console.warn('Trace:', trace)
}

// 全局属性
app.config.globalProperties.$ELEMENT = {
  size: 'default',
  zIndex: 3000,
}

// 异步初始化应用
const initApp = async () => {
  try {
    // 🔥 关键步骤：在应用启动前加载环境配置
    await envUtil.loadConfig();

    // 现在您可以在 Vue 组件中使用 envService.getApiBaseUrl()
    // 来获取动态配置的后端地址
    console.log('🚀 当前 API 地址:', envUtil.getApiBaseUrl());

    // 挂载应用
    app.mount('#app')

    // 应用初始化完成的事件处理
    document.addEventListener('DOMContentLoaded', () => {
      const loader = document.getElementById('initial-loader')
      if (loader) {
        loader.style.opacity = '0'
        setTimeout(() => loader.remove(), 300)
      }
      document.body.classList.add('app-ready')
    })

  } catch (error) {
    console.error('❌ 应用初始化失败:', error)
    document.body.innerHTML = `
      <div style="padding: 20px; text-align: center; color: red;">
        <h2>应用启动失败</h2>
        <p>请检查配置文件是否正确</p>
        <pre>${error}</pre>
      </div>
    `
  }
}

// 启动应用
initApp()

// 键盘快捷键支持
document.addEventListener('keydown', (event) => {
  // Ctrl/Cmd + K 打开搜索
  if ((event.ctrlKey || event.metaKey) && event.key === 'k') {
    event.preventDefault()
    // 触发搜索功能
    const searchInput = document.querySelector('.search-input input') as HTMLInputElement
    if (searchInput) {
      searchInput.focus()
    }
  }

  // ESC 键关闭模态框
  if (event.key === 'Escape') {
    // Element Plus 会自动处理，这里可以添加自定义逻辑
  }
})

// 网络状态监听
window.addEventListener('online', () => {
  console.log('网络已连接')
  // 可以在这里重新发送失败的请求
})

window.addEventListener('offline', () => {
  console.log('网络已断开')
  // 可以在这里显示离线提示
})

// 页面可见性变化监听
document.addEventListener('visibilitychange', () => {
  if (document.hidden) {
    console.log('页面隐藏')
    // 页面被隐藏时的处理逻辑
  } else {
    console.log('页面显示')
    // 页面重新显示时的处理逻辑
    // 比如刷新数据
  }
})

// 内存泄漏预防
window.addEventListener('beforeunload', () => {
  // 清理定时器、事件监听器等
  // pinia stores 会自动清理
})

// 类型声明扩展
declare global {
  interface Window {
    __APP__: any
    __ROUTER__: any
    __PINIA__: any
  }
}

export default app
