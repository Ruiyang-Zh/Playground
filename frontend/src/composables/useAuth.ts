import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

export function useAuth() {
  const router = useRouter()
  const authStore = useAuthStore()

  const isLoggedIn = computed(() => authStore.isLoggedIn)
  const currentUser = computed(() => authStore.currentUser)
  const loading = computed(() => authStore.loading)

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
    authStore.logout()
    ElMessage.success('已退出登录')
    router.push('/auth/login')
  }

  return {
    isLoggedIn,
    currentUser,
    loading,
    requireAuth,
    logout
  }
}
