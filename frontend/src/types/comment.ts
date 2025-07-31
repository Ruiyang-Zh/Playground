import type { UserPublicInfoResponse } from './user'

export interface CommentResponse {
  id: number
  content: string
  user: UserPublicInfoResponse
  parentId?: number
  parentUserName?: string
  parentContent?: string
  createdAt: string
}

export interface CommentCreateRequest {
  content: string
  parentId?: number
}
