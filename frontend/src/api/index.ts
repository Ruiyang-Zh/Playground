// 认证相关API
export * as authAPI from './auth'

// 用户相关API
export * as userAPI from './user'

// 活动相关API
export * as activityAPI from './activity'

// 评论相关API
export * as commentAPI from './comment'

// 统一导出类型
export type { ApiResponse, PageResponse } from '@/types/api'
export type { UserLoginRequest, UserInfoResponse, UserPublicInfoResponse } from '@/types/user'
export type { ActivityBriefResponse, ActivityDetailResponse, ActivitySearchParams } from '@/types/activity'
export type { CommentResponse, CommentCreateRequest } from '@/types/comment'
