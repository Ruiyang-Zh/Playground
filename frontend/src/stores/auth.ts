import { defineStore } from 'pinia'
import { ref, computed, readonly } from 'vue'
import * as authAPI from '@/api/auth'
import type { UserLoginRequest, UserInfoResponse } from '@/types/user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const currentUser = ref<UserInfoResponse | null>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value)

  const login = async (credentials: UserLoginRequest) => {
    try {
      loading.value = true
      const response = await authAPI.login(credentials)
      token.value = response.data
      localStorage.setItem('token', response.data)

      // 获取用户信息
      await fetchUserInfo()
    } catch (error) {
      console.error('Login failed:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    token.value = null
    currentUser.value = null
    localStorage.removeItem('token')
  }

  const fetchUserInfo = async () => {
    try {
      const response = await authAPI.getUserInfo()
      currentUser.value = response.data
    } catch (error) {
      console.error('Failed to fetch user info:', error)
      // 如果获取用户信息失败，可能token已过期
      logout()
    }
  }

  // 初始化时如果有token就获取用户信息
  if (token.value) {
    fetchUserInfo()
  }

  return {
    token: readonly(token),
    currentUser: readonly(currentUser),
    loading: readonly(loading),
    isLoggedIn,
    login,
    logout,
    fetchUserInfo
  }
})
