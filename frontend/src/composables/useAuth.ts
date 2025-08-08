import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import type { UserLoginRequest } from '@/types/user'

export function useAuth() {
  const router = useRouter()
  const authStore = useAuthStore()

  const isLoggedIn = computed(() => authStore.isLoggedIn)
  const currentUser = computed(() => authStore.currentUser)
  const loading = computed(() => authStore.loading)

  // 登录方法 - 调用 store 的登录方法
  const login = async (credentials: UserLoginRequest) => {
    try {
      await authStore.login(credentials)
      console.log('Login completed, user:', authStore.currentUser)
    } catch (error) {
      console.error('Login failed in useAuth:', error)
      throw error
    }
  }

  const requireAuth = (callback?: () => void | Promise<void>) => {
    if (!isLoggedIn.value) {
      ElMessage.warning('请先登录')
      router.push({
        path: '/auth/login',
        query: { redirect: router.currentRoute.value.fullPath }
      })
      return false
    }
    if (callback) {
      const result = callback()
      if (result instanceof Promise) {
        return result
      }
    }
    return true
  }

  const logout = async () => {
    try {
      authStore.logout()
      ElMessage.success('已退出登录')

      // 跳转到首页
      if (router.currentRoute.value.meta.requiresAuth) {
        router.push('/')
      }
    } catch (error) {
      console.error('Logout failed:', error)
      ElMessage.error('退出登录失败')
    }
  }

  return {
    isLoggedIn,
    currentUser,
    loading,
    login, // 导出登录方法
    requireAuth,
    logout
  }
}
