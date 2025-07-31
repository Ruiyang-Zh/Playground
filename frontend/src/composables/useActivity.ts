import { ref, computed } from 'vue'
import { useActivityStore } from '@/stores/activity'
import { useAuth } from './useAuth'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ActivitySearchParams } from '@/types/activity'

export function useActivity() {
  const activityStore = useActivityStore()
  const { requireAuth } = useAuth()

  const searchParams = ref<ActivitySearchParams>({
    page: 0,
    size: 10,
    sortBy: 'startTime',
    sortDir: 'asc'
  })

  const activities = computed(() => activityStore.activities)
  const currentActivity = computed(() => activityStore.currentActivity)
  const loading = computed(() => activityStore.loading)
  const pagination = computed(() => activityStore.pagination)

  const searchActivities = async (params?: Partial<ActivitySearchParams>) => {
    const finalParams = { ...searchParams.value, ...params }
    searchParams.value = finalParams
    return await activityStore.fetchActivities(finalParams)
  }

  const registerActivity = async (activityId: number) => {
    return requireAuth(async () => {
      try {
        await activityStore.registerActivity(activityId)
        ElMessage.success('报名成功！')
      } catch (error) {
        ElMessage.error('报名失败，请重试')
        throw error
      }
    })
  }

  const unregisterActivity = async (activityId: number) => {
    return requireAuth(async () => {
      try {
        await ElMessageBox.confirm('确定要退出这个活动吗？', '确认操作', {
          type: 'warning'
        })
        await activityStore.unregisterActivity(activityId)
        ElMessage.success('已退出活动')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('退出失败，请重试')
          throw error
        }
      }
    })
  }

  const cancelActivity = async (activityId: number) => {
    return requireAuth(async () => {
      try {
        await ElMessageBox.confirm('确定要取消这个活动吗？此操作不可撤销！', '确认操作', {
          type: 'warning',
          confirmButtonText: '确定取消',
          cancelButtonText: '保留活动'
        })
        await activityStore.cancelActivity(activityId)
        ElMessage.success('活动已取消')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('取消失败，请重试')
          throw error
        }
      }
    })
  }

  return {
    activities,
    currentActivity,
    loading,
    pagination,
    searchParams,
    searchActivities,
    registerActivity,
    unregisterActivity,
    cancelActivity
  }
}
