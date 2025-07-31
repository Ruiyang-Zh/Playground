import request from '@/utils/request'
import type { ApiResponse, PageResponse } from '@/types/api'
import type { ActivityBriefResponse, ActivityDetailResponse, ActivitySearchParams, ParticipationType } from '@/types/activity'

export const getActivities = (params: ActivitySearchParams) => {
  return request.get<ApiResponse<PageResponse<ActivityBriefResponse>>>('/activities', { params })
}

export const getActivityDetail = (id: number) => {
  return request.get<ApiResponse<ActivityDetailResponse>>(`/activities/${id}`)
}

export const createActivity = (data: FormData) => {
  return request.post<ApiResponse<number>>('/activities', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const updateActivity = (id: number, data: FormData) => {
  return request.put<ApiResponse<void>>(`/activities/${id}`, data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const deleteActivity = (id: number) => {
  return request.delete<ApiResponse<void>>(`/activities/${id}`)
}

export const registerActivity = (id: number) => {
  return request.post<ApiResponse<void>>(`/activities/${id}/register`)
}

export const unregisterActivity = (id: number) => {
  return request.delete<ApiResponse<void>>(`/activities/${id}/register`)
}

export const getUserActivities = (userId: number, params: {
  participationType?: ParticipationType
  activityType?: string
  activityStatus?: string
  startDate?: string
  endDate?: string
  province?: string
  city?: string
  sortBy?: string
  sortDir?: string
  page: number
  size: number
}) => {
  return request.get<ApiResponse<PageResponse<ActivityBriefResponse>>>(`/activities/users/${userId}`, { params })
}

export const getMyActivities = (params: {
  participationType?: ParticipationType
  activityType?: string
  activityStatus?: string
  startDate?: string
  endDate?: string
  province?: string
  city?: string
  sortBy?: string
  sortDir?: string
  page: number
  size: number
}) => {
  return request.get<ApiResponse<PageResponse<ActivityBriefResponse>>>('/activities/users/my', { params })
}
