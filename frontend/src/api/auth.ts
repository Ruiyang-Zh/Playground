import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'
import type { UserLoginRequest, UserInfoResponse } from '@/types/user'

export const login = (data: UserLoginRequest) => {
  return request.post<ApiResponse<string>>('/auth/login', data)
}

export const register = (data: FormData) => {
  return request.post<ApiResponse<void>>('/auth/register', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const getUserInfo = () => {
  return request.get<ApiResponse<UserInfoResponse>>('/users/info')
}
