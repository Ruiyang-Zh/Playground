<template>
  <aside class="activity-sidebar">
    <div class="sidebar-content">
      <!-- 活动基本信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h3 class="card-title">活动信息</h3>
        </div>

        <div class="info-list" v-if="activity">
          <div class="info-item">
            <el-icon class="info-icon"><Clock /></el-icon>
            <div class="info-content">
              <span class="info-label">开始时间</span>
              <span class="info-value">{{ formatDateTimeValue(activity.startTime) }}</span>
            </div>
          </div>

          <div class="info-item">
            <el-icon class="info-icon"><Clock /></el-icon>
            <div class="info-content">
              <span class="info-label">结束时间</span>
              <span class="info-value">{{ formatDateTimeValue(activity.endTime) }}</span>
            </div>
          </div>

          <div class="info-item">
            <el-icon class="info-icon"><Location /></el-icon>
            <div class="info-content">
              <span class="info-label">活动地点</span>
              <span class="info-value">{{ getFullAddress(activity) }}</span>
            </div>
          </div>

          <div class="info-item">
            <el-icon class="info-icon"><User /></el-icon>
            <div class="info-content">
              <span class="info-label">参与人数</span>
              <span class="info-value">
                {{ activity.currentParticipants }}/{{ activity.maxParticipants }}人
                <span class="min-participants" v-if="activity.minParticipants > 0">
                  (最少{{ activity.minParticipants }}人)
                </span>
              </span>
            </div>
          </div>

          <div class="info-item">
            <el-icon class="info-icon"><Money /></el-icon>
            <div class="info-content">
              <span class="info-label">活动费用</span>
              <span class="info-value">
                {{ activity.fee > 0 ? `¥${activity.fee}` : '免费' }}
              </span>
            </div>
          </div>

          <div class="info-item">
            <el-icon class="info-icon"><Calendar /></el-icon>
            <div class="info-content">
              <span class="info-label">报名截止</span>
              <span class="info-value">{{ formatDateTimeValue(activity.registrationDeadline) }}</span>
            </div>
          </div>

          <div class="info-item" v-if="activity.contactInfo">
            <el-icon class="info-icon"><Phone /></el-icon>
            <div class="info-content">
              <span class="info-label">联系方式</span>
              <span class="info-value">{{ activity.contactInfo }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 参与者列表 -->
      <div class="participants-card" v-if="activity?.participants?.length">
        <div class="card-header">
          <h3 class="card-title">参与者 ({{ activity.participants.length }})</h3>
        </div>

        <div class="participants-grid">
          <UserCard
            v-for="participant in activity.participants"
            :key="participant.id"
            :user-id="participant.id"
          >
            <UserAvatar
              :user="participant"
              size="medium"
              :show-hover="false"
              class="participant-avatar"
            />
          </UserCard>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons" v-if="activity">
        <!-- 报名按钮 -->
        <el-button
          v-if="activity.canRegister && !activity.isRegistered"
          type="primary"
          size="large"
          @click="$emit('register')"
          :loading="registering"
          class="action-btn register-btn"
        >
          <el-icon><Plus /></el-icon>
          立即报名
        </el-button>

        <!-- 已报名状态 -->
        <el-button
          v-else-if="activity.isRegistered"
          type="success"
          size="large"
          class="action-btn registered-btn"
          disabled
        >
          <el-icon><Check /></el-icon>
          已报名
        </el-button>

        <!-- 不可报名状态 -->
        <el-button
          v-else
          size="large"
          class="action-btn disabled-btn"
          disabled
        >
          <el-icon><Lock /></el-icon>
          {{ getDisabledText() }}
        </el-button>

        <!-- 管理按钮 - 仅创建者可见 -->
        <template v-if="activity.canUpdate || activity.canCancel">
          <el-button
            v-if="activity.canUpdate"
            type="warning"
            size="large"
            @click="$emit('edit')"
            class="action-btn edit-btn"
          >
            <el-icon><Edit /></el-icon>
            编辑活动
          </el-button>

          <el-button
            v-if="activity.canCancel"
            type="danger"
            size="large"
            @click="$emit('cancel')"
            class="action-btn cancel-btn"
          >
            <el-icon><Delete /></el-icon>
            取消活动
          </el-button>
        </template>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  Clock,
  Location,
  User,
  Money,
  Calendar,
  Phone,
  Plus,
  Check,
  Lock,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import UserAvatar from '../common/UserAvatar.vue'
import UserCard from '../common/UserCard.vue'
import { formatDateTimeValue } from '@/utils/format'
import type { ActivityDetailResponse } from '@/types/activity'

interface Props {
  activity?: ActivityDetailResponse | null
  registering?: boolean
}

interface Emits {
  (e: 'register'): void
  (e: 'edit'): void
  (e: 'cancel'): void
}

const props = withDefaults(defineProps<Props>(), {
  registering: false
})

const emit = defineEmits<Emits>()

const getFullAddress = (activity: ActivityDetailResponse) => {
  const parts = [activity.province, activity.city, activity.district, activity.venue]
  return parts.filter(Boolean).join(' ')
}

const getDisabledText = () => {
  if (!props.activity) return '无法报名'

  switch (props.activity.status) {
    case 'FULL':
      return '活动已满'
    case 'REGISTRATION_CLOSED':
      return '报名已截止'
    case 'IN_PROGRESS':
      return '活动进行中'
    case 'COMPLETED':
      return '活动已结束'
    case 'CANCELLED':
      return '活动已取消'
    default:
      return '无法报名'
  }
}
</script>

<style scoped>
.activity-sidebar {
  width: 320px;
  flex-shrink: 0;
  padding: var(--spacing-xl);
  background: var(--bg-color-secondary);
  border-left: 1px solid var(--border-color-light);
  height: calc(100vh - var(--header-height));
  overflow-y: auto;
  position: sticky;
  top: var(--header-height);
}

.sidebar-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.info-card,
.participants-card {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  border: 1px solid var(--border-color-light);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.card-header {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--border-color-light);
  background: var(--bg-color-secondary);
}

.card-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
  margin: 0;
}

.info-list {
  padding: var(--spacing-lg) var(--spacing-xl);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
}

.info-icon {
  font-size: var(--font-size-lg);
  color: var(--text-color-tertiary);
  margin-top: 2px;
  flex-shrink: 0;
}

.info-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  min-width: 0;
}

.info-label {
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
  font-weight: var(--font-weight-medium);
}

.info-value {
  font-size: var(--font-size-base);
  color: var(--text-color-primary);
  font-weight: var(--font-weight-medium);
  line-height: 1.4;
  word-break: break-all;
}

.min-participants {
  font-size: var(--font-size-sm);
  color: var(--text-color-secondary);
  font-weight: var(--font-weight-normal);
}

.participants-grid {
  padding: var(--spacing-lg) var(--spacing-xl);
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(50px, 1fr));
  gap: var(--spacing-md);
}

.participant-avatar {
  transition: var(--transition-base);
  cursor: pointer;
}

.participant-avatar:hover {
  transform: scale(1.1);
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.action-btn {
  width: 100%;
  border-radius: var(--border-radius-lg);
  font-weight: var(--font-weight-semibold);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  transition: var(--transition-base);
}

.register-btn {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.register-btn:hover {
  background: var(--primary-light);
  border-color: var(--primary-light);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
}

.registered-btn {
  background: var(--success-color);
  border-color: var(--success-color);
  color: white;
}

.disabled-btn {
  background: var(--bg-color-secondary);
  color: var(--text-color-tertiary);
  border-color: var(--border-color);
}

.edit-btn {
  background: var(--warning-color);
  border-color: var(--warning-color);
  color: white;
}

.edit-btn:hover {
  background: var(--warning-light);
  border-color: var(--warning-light);
  transform: translateY(-1px);
}

.cancel-btn {
  background: var(--danger-color);
  border-color: var(--danger-color);
  color: white;
}

.cancel-btn:hover {
  background: var(--danger-light);
  border-color: var(--danger-light);
  transform: translateY(-1px);
}

/* 滚动条样式 */
.activity-sidebar::-webkit-scrollbar {
  width: 4px;
}

.activity-sidebar::-webkit-scrollbar-track {
  background: var(--bg-color-secondary);
}

.activity-sidebar::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 2px;
}

.activity-sidebar::-webkit-scrollbar-thumb:hover {
  background: var(--text-color-tertiary);
}

@media (max-width: 1024px) {
  .activity-sidebar {
    display: none;
  }
}
</style>
