import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'
import type { UserInfoResponse, UserPublicInfoResponse, PasswordChangeRequest } from '@/types/user'

export const updateUserInfo = (data: FormData) => {
  return request.put<ApiResponse<UserInfoResponse>>('/users/info', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const getUserPublicInfo = (userId: number) => {
  return request.get<ApiResponse<UserPublicInfoResponse>>(`/users/${userId}/info`)
}

export const changePassword = (data: PasswordChangeRequest) => {
  return request.put<ApiResponse<void>>('/users/password', data)
}
