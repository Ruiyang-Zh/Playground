export type SportsType =
  | 'BASKETBALL'
  | 'BADMINTON'
  | 'RUNNING'
  | 'SWIMMING'
  | 'FOOTBALL'
  | 'TENNIS'
  | 'TABLE_TENNIS'
  | 'CYCLING'
  | 'HIKING'
  | 'YOGA'
  | 'FITNESS'
  | 'OTHER'

export interface UserLoginRequest {
  account: string
  password: string
}

export interface UserInfoResponse {
  id: number
  phone: string
  email: string
  username: string
  avatar: string
  sportsPreference: SportsType[]
  description: string
  createdAt: string
}

export interface UserPublicInfoResponse {
  id: number
  username: string
  avatar: string
  sportsPreference: SportsType[]
  description: string
}

export interface PasswordChangeRequest {
  oldPassword: string
  newPassword: string
}
