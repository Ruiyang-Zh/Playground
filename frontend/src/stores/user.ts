import { defineStore } from 'pinia'
import { ref , readonly } from 'vue'
import * as userAPI from '@/api/user'
import type { UserPublicInfoResponse, PasswordChangeRequest, UserInfoResponse } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  const userProfiles = ref<Map<number, UserPublicInfoResponse>>(new Map())
  const loading = ref(false)
  const currentUserId = ref<number | null>(null)

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

  // 在现有的 useUserStore 中添加以下方法
  const updateUserInfo = async (data: FormData): Promise<UserInfoResponse> => {
    try {
      loading.value = true
      const response = await userAPI.updateUserInfo(data)

      // 更新缓存中的用户信息
      const userId = response.data.id
      const publicInfo: UserPublicInfoResponse = {
        id: response.data.id,
        username: response.data.username,
        avatar: response.data.avatar,
        sportsPreference: response.data.sportsPreference,
        description: response.data.description
      }
      userProfiles.value.set(userId, publicInfo)

      return response.data
    } catch (error) {
      console.error('Failed to update user info:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updatePassword = async (data: PasswordChangeRequest): Promise<void> => {
    try {
      loading.value = true
      await userAPI.changePassword(data)
      return
    } catch (error) {
      console.error('Failed to change password:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    userProfiles: readonly(userProfiles),
    loading: readonly(loading),
    fetchUserProfile,
    getUserProfile,
    updateUserInfo, // 新增
    updatePassword,
    clearCache
  }
})
