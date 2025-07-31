import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import type { UserPublicInfoResponse } from '@/types/user'

export function useUserInfo() {
  const userStore = useUserStore()

  const loading = computed(() => userStore.loading)

  const fetchUserInfo = async (userId: number): Promise<UserPublicInfoResponse> => {
    return await userStore.fetchUserProfile(userId)
  }

  const getUserInfo = (userId: number): UserPublicInfoResponse | undefined => {
    return userStore.getUserProfile(userId)
  }

  const clearUserCache = () => {
    userStore.clearCache()
  }

  return {
    loading,
    fetchUserInfo,
    getUserInfo,
    clearUserCache
  }
}
