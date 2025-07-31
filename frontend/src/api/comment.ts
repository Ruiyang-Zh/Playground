import request from '@/utils/request'
import type { ApiResponse, PageResponse } from '@/types/api'
import type { CommentResponse, CommentCreateRequest } from '@/types/comment'

export const getActivityComments = (activityId: number, params: {
  page: number
  size: number
  sortByNewest: boolean
}) => {
  return request.get<ApiResponse<PageResponse<CommentResponse>>>(`/activities/${activityId}/comments`, { params })
}

export const createComment = (activityId: number, data: CommentCreateRequest) => {
  return request.post<ApiResponse<CommentResponse>>(`/activities/${activityId}/comments`, data)
}

export const deleteComment = (activityId: number, commentId: number) => {
  return request.delete<ApiResponse<void>>(`/activities/${activityId}/comments/${commentId}`)
}

export const getCommentCount = (activityId: number) => {
  return request.get<ApiResponse<number>>(`/activities/${activityId}/comments/count`)
}
