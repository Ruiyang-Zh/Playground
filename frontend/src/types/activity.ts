import type { UserPublicInfoResponse, SportsType } from './user'

export type ActivityStatus =
  | 'RECRUITING'
  | 'FULL'
  | 'REGISTRATION_CLOSED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED'

export type ParticipationType =
  | 'CREATED'
  | 'JOINED'
  | 'APPLIED'
  | 'CANCELLED'

export interface ActivityBriefResponse {
  id: number
  title: string
  type: SportsType
  startTime: string
  endTime: string
  province: string
  city: string
  district: string
  venue: string
  currentParticipants: number
  maxParticipants: number
  fee: number
  status: ActivityStatus
  registrationDeadline: string
  creator: UserPublicInfoResponse
  isRegistered: boolean
  image: string
  createdAt: string
}

export interface ActivityDetailResponse {
  id: number
  title: string
  type: SportsType
  description: string
  images: string[]
  startTime: string
  endTime: string
  province: string
  city: string
  district: string
  venue: string
  minParticipants: number
  maxParticipants: number
  currentParticipants: number
  fee: number
  registrationDeadline: string
  requirements: string
  status: ActivityStatus
  creator: UserPublicInfoResponse
  contactInfo: string
  participants: UserPublicInfoResponse[]
  isRegistered: boolean
  canRegister: boolean
  canUpdate: boolean
  canCancel: boolean
  createdAt: string
  updatedAt: string
}

export interface ActivitySearchParams {
  keyword?: string
  type?: SportsType
  startTime?: string
  endTime?: string
  minFee?: string
  maxFee?: string
  province?: string
  city?: string
  district?: string
  status?: ActivityStatus
  sortBy?: string
  sortDir?: string
  page: number
  size: number
}
