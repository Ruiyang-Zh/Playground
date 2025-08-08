import type { Router } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

export function setupRouterGuards(router: Router) {
  // 全局前置守卫
  router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()

    // 设置页面标题
    if (to.meta.title) {
      document.title = `${to.meta.title} - Playground`
    }

    // 检查需要认证的路由
    if (to.meta.requiresAuth && !authStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      next({
        path: '/auth/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    // 如果已登录，不允许访问登录页
    if (authStore.isLoggedIn && to.path.startsWith('/auth/')) {
      next('/')
      return
    }

    next()
  })

  // 全局后置钩子
  router.afterEach((to, from) => {
    // 页面切换后滚动到顶部
    window.scrollTo(0, 0)
  })
}
