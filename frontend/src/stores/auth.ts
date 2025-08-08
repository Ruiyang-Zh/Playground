import { defineStore } from 'pinia'
import { ref, computed, readonly } from 'vue'
import * as authAPI from '@/api/auth'
import type { UserLoginRequest, UserInfoResponse } from '@/types/user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const currentUser = ref<UserInfoResponse | null>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value && !!currentUser.value)

  const login = async (credentials: UserLoginRequest) => {
    try {
      loading.value = true
      const response = await authAPI.login(credentials)

      // 设置token
      token.value = response.data
      localStorage.setItem('token', response.data)

      // 获取用户信息
      await fetchUserInfo()

      console.log('Login successful, user:', currentUser.value)
    } catch (error) {
      console.error('Login failed:', error)
      // 清理状态
      token.value = null
      currentUser.value = null
      localStorage.removeItem('token')
      throw error
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    token.value = null
    currentUser.value = null
    localStorage.removeItem('token')
    console.log('User logged out')
  }

  const fetchUserInfo = async () => {
    if (!token.value) return

    try {
      const response = await authAPI.getUserInfo()
      currentUser.value = response.data
      console.log('User info fetched:', response.data)
    } catch (error) {
      console.error('Failed to fetch user info:', error)
      // 如果获取用户信息失败，可能token已过期
      logout()
      throw error
    }
  }

  // 初始化时如果有token就获取用户信息
  if (token.value && !currentUser.value) {
    fetchUserInfo().catch(() => {
      // 静默处理初始化失败
      logout()
    })
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
