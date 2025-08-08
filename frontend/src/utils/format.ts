import dayjs from 'dayjs'
import type { SportsType, ActivityStatus } from '@/types/activity'

export const formatDateTimeValue = (date: string) => {
  return dayjs(date).format('YYYY-MM-DDTHH:mm:ss')
}

export const formatDisplayDateTime = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

export const formatRelativeTime = (date: string) => {
  const now = dayjs();
  const target = dayjs(date);

  const seconds = now.diff(target, 'seconds');
  if (seconds < 60) {
    return `${seconds}秒前`;
  }

  const minutes = now.diff(target, 'minutes');
  if (minutes < 60) {
    return `${minutes}分钟前`;
  }

  const hours = now.diff(target, 'hours');
  if (hours < 24) {
    return `${hours}小时前`;
  }

  const days = now.diff(target, 'days');
  return `${days}天前`;
}

export const getActivityTypeText = (type: SportsType): string => {
  const typeMap: Record<SportsType, string> = {
    BASKETBALL: '篮球',
    BADMINTON: '羽毛球',
    RUNNING: '跑步',
    SWIMMING: '游泳',
    FOOTBALL: '足球',
    TENNIS: '网球',
    TABLE_TENNIS: '乒乓球',
    CYCLING: '骑行',
    HIKING: '徒步',
    YOGA: '瑜伽',
    FITNESS: '健身',
    OTHER: '其他'
  }
  return typeMap[type] || '未知'
}

export const getStatusText = (status: ActivityStatus): string => {
  const statusMap: Record<ActivityStatus, string> = {
    RECRUITING: '招募中',
    FULL: '已满员',
    REGISTRATION_CLOSED: '报名结束',
    IN_PROGRESS: '进行中',
    COMPLETED: '已结束',
    CANCELLED: '已取消'
  }
  return statusMap[status] || '未知'
}

export const getStatusType = (status: ActivityStatus | undefined): string => {
  if (!status) return 'info'

  const typeMap: Record<ActivityStatus, string> = {
    RECRUITING: 'success',
    FULL: 'warning',
    REGISTRATION_CLOSED: 'info',
    IN_PROGRESS: 'primary',
    COMPLETED: 'info',
    CANCELLED: 'danger'
  }
  return typeMap[status] || 'info'
}
