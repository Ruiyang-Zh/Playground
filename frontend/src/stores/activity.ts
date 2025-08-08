import { defineStore } from 'pinia'
import { ref , readonly } from 'vue'
import * as activityAPI from '@/api/activity'
import type {
  ActivityBriefResponse,
  ActivityDetailResponse,
  ActivitySearchParams, ParticipationType
} from '@/types/activity'
import type { PageResponse } from '@/types/api'

export const useActivityStore = defineStore('activity', () => {
  const activities = ref<ActivityBriefResponse[]>([])
  const currentActivity = ref<ActivityDetailResponse | null>(null)
  const recentActivities = ref<ActivityBriefResponse[]>([])
  const loading = ref(false)
  const pagination = ref({
    page: 0,
    size: 10,
    total: 0,
    totalPages: 0
  })

  const fetchActivities = async (params: ActivitySearchParams) => {
    try {
      loading.value = true
      const response = await activityAPI.getActivities(params)
      activities.value = response.data.content || []
      pagination.value = {
        page: response.data.number,
        size: response.data.size,
        total: response.data.totalElements,
        totalPages: response.data.totalPages
      }
      return response.data
    } catch (error) {
      console.error('Failed to fetch activities:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchActivityDetail = async (id: number) => {
    try {
      loading.value = true
      const response = await activityAPI.getActivityDetail(id)
      currentActivity.value = response.data
      return response.data
    } catch (error) {
      console.error('Failed to fetch activity detail:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchUserActivities = async (participationType? : ParticipationType) => {
    try {
      const response = await activityAPI.getMyActivities({
        participationType: participationType,
        page: 0,
        size: 10,
        sortBy: 'startTime',
        sortDir: 'desc'
      })
      recentActivities.value = response.data.content || []
      return response.data
    } catch (error) {
      console.error('Failed to fetch user activities:', error)
      throw error
    }
  }

  const registerActivity = async (id: number) => {
    try {
      await activityAPI.registerActivity(id)
      // 重新获取活动详情以更新状态
      if (currentActivity.value?.id === id) {
        await fetchActivityDetail(id)
      }
    } catch (error) {
      console.error('Failed to register activity:', error)
      throw error
    }
  }

  const unregisterActivity = async (id: number) => {
    try {
      await activityAPI.unregisterActivity(id)
      // 重新获取活动详情以更新状态
      if (currentActivity.value?.id === id) {
        await fetchActivityDetail(id)
      }
    } catch (error) {
      console.error('Failed to unregister activity:', error)
      throw error
    }
  }

  const cancelActivity = async (id: number) => {
    try {
      await activityAPI.deleteActivity(id)
      currentActivity.value = null
    } catch (error) {
      console.error('Failed to cancel activity:', error)
      throw error
    }
  }

  return {
    activities: readonly(activities),
    currentActivity: readonly(currentActivity),
    recentActivities: readonly(recentActivities),
    loading: readonly(loading),
    pagination: readonly(pagination),
    fetchActivities,
    fetchActivityDetail,
    fetchUserActivities,
    registerActivity,
    unregisterActivity,
    cancelActivity
  }
})
