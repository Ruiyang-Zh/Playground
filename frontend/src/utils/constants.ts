import type { SportsType, ActivityStatus } from '@/types/activity'

export const SPORTS_OPTIONS: Array<{ label: string; value: SportsType }> = [
  { label: '篮球', value: 'BASKETBALL' },
  { label: '羽毛球', value: 'BADMINTON' },
  { label: '跑步', value: 'RUNNING' },
  { label: '游泳', value: 'SWIMMING' },
  { label: '足球', value: 'FOOTBALL' },
  { label: '网球', value: 'TENNIS' },
  { label: '乒乓球', value: 'TABLE_TENNIS' },
  { label: '骑行', value: 'CYCLING' },
  { label: '徒步', value: 'HIKING' },
  { label: '瑜伽', value: 'YOGA' },
  { label: '健身', value: 'FITNESS' },
  { label: '其他', value: 'OTHER' }
]

export const STATUS_OPTIONS: Array<{ label: string; value: ActivityStatus }> = [
  { label: '招募中', value: 'RECRUITING' },
  { label: '已满员', value: 'FULL' },
  { label: '报名结束', value: 'REGISTRATION_CLOSED' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '已结束', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]

export const SORT_OPTIONS = [
  { label: '开始时间', value: 'startTime' },
  { label: '创建时间', value: 'createdAt' }
]

export const SORT_DIR_OPTIONS = [
  { label: '升序', value: 'asc' },
  { label: '降序', value: 'desc' }
]
