import { defineStore } from 'pinia'
import { ref , readonly } from 'vue'
import * as userAPI from '@/api/user'
import type { UserPublicInfoResponse } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  const userProfiles = ref<Map<number, UserPublicInfoResponse>>(new Map())
  const loading = ref(false)

  const fetchUserProfile = async (userId: number) => {
    // 如果已经缓存了用户信息，直接返回
    if (userProfiles.value.has(userId)) {
      return userProfiles.value.get(userId)!
    }

    try {
      loading.value = true
      const response = await userAPI.getUserPublicInfo(userId)
      userProfiles.value.set(userId, response.data)
      return response.data
    } catch (error) {
      console.error('Failed to fetch user profile:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getUserProfile = (userId: number) => {
    return userProfiles.value.get(userId)
  }

  const clearCache = () => {
    userProfiles.value.clear()
  }

  return {
    userProfiles: readonly(userProfiles),
    loading: readonly(loading),
    fetchUserProfile,
    getUserProfile,
    clearCache
  }
})
