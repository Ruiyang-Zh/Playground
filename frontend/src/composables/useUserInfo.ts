import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import type { UserPublicInfoResponse, UserInfoResponse, PasswordChangeRequest } from '@/types/user'

export function useUserInfo() {
  const userStore = useUserStore()

  const loading = computed(() => userStore.loading)

  const fetchUserInfo = async (userId: number): Promise<UserPublicInfoResponse> => {
    return await userStore.fetchUserProfile(userId)
  }

  const getUserInfo = (userId: number): UserPublicInfoResponse | undefined => {
    return userStore.getUserProfile(userId)
  }

  // 更新用户信息
  const updateUserInfo = async (data: FormData): Promise<UserInfoResponse> => {
    return await userStore.updateUserInfo(data)
  }

  const updatePassword = async (data: PasswordChangeRequest): Promise<void> => {
    return await userStore.updatePassword(data)
  }

  const clearUserCache = () => {
    userStore.clearCache()
  }

  return {
    loading,
    fetchUserInfo,
    getUserInfo,
    updateUserInfo,
    updatePassword,
    clearUserCache
  }
}
