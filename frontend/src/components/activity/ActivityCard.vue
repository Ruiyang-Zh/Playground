<template>
  <div class="activity-card" @click="handleViewDetail">
    <!-- 活动图片 -->
    <div class="card-image">
      <img
        v-if="activity.image"
        :src="activity.image"
        :alt="activity.title"
        class="activity-image"
      />
      <div v-else class="image-placeholder">
        <el-icon class="placeholder-icon"><Picture /></el-icon>
      </div>

      <!-- 状态标签 -->
      <div class="status-badge">
        <el-tag
          :type="getStatusType(activity.status)"
          size="small"
          class="status-tag"
        >
          {{ getStatusText(activity.status) }}
        </el-tag>
      </div>
    </div>

    <!-- 活动信息 -->
    <div class="card-content">
      <div class="activity-header">
        <h3 class="activity-title">{{ activity.title }}</h3>
        <div class="activity-type">
          <el-tag type="info" size="small">
            {{ getActivityTypeText(activity.type) }}
          </el-tag>
        </div>
      </div>

      <div class="activity-meta">
        <div class="meta-item">
          <el-icon class="meta-icon"><Clock /></el-icon>
          <span>{{ formatDisplayDateTime(activity.startTime) }}</span>
        </div>
        <div class="meta-item">
          <el-icon class="meta-icon"><Location /></el-icon>
          <span>{{ activity.city + ' ' + activity.district + ' '  + activity.venue }}</span>
        </div>
        <div class="meta-item">
          <el-icon class="meta-icon"><User /></el-icon>
          <span>{{ activity.currentParticipants }}/{{ activity.maxParticipants }}人</span>
        </div>
      </div>

      <!-- 创建者信息 -->
      <div class="creator-info">
        <UserAvatar
          :user="activity.creator"
          size="small"
          :show-hover="true"
        />
        <span class="creator-name">{{ activity.creator.username }}</span>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="card-actions">
      <!-- 可以报名 -->
      <el-button
        v-if="!activity.isRegistered && canRegister"
        type="primary"
        size="small"
        class="action-btn register-btn"
        @click.stop="handleRegister"
        :loading="registering"
      >
        <el-icon><Plus /></el-icon>
        报名参加
      </el-button>

      <!-- 已报名 - 显示状态和取消报名按钮 -->
      <template v-else-if="activity.isRegistered">
        <el-button
          type="success"
          size="small"
          class="action-btn registered-btn"
          disabled
        >
          <el-icon><Check /></el-icon>
          已报名
        </el-button>

        <!-- 取消报名按钮 -->
        <el-button
          v-if="canUnregister"
          type="warning"
          size="small"
          class="action-btn unregister-btn"
          @click.stop="handleUnregister"
          :loading="unregistering"
        >
          <el-icon><Close /></el-icon>
          退出
        </el-button>
      </template>

      <!-- 活动已满 -->
      <el-button
        v-else-if="activity.status === 'FULL'"
        size="small"
        class="action-btn full-btn"
        disabled
      >
        <el-icon><Lock /></el-icon>
        活动已满
      </el-button>

      <!-- 报名已截止 -->
      <el-button
        v-else-if="activity.status === 'REGISTRATION_CLOSED'"
        size="small"
        class="action-btn closed-btn"
        disabled
      >
        <el-icon><Lock /></el-icon>
        报名截止
      </el-button>

      <!-- 活动进行中 -->
      <el-button
        v-else-if="activity.status === 'IN_PROGRESS'"
        size="small"
        class="action-btn progress-btn"
        disabled
      >
        <el-icon><Timer /></el-icon>
        进行中
      </el-button>

      <!-- 活动已完成 -->
      <el-button
        v-else-if="activity.status === 'COMPLETED'"
        size="small"
        class="action-btn completed-btn"
        disabled
      >
        <el-icon><CircleCheck /></el-icon>
        已完成
      </el-button>

      <!-- 活动已取消 -->
      <el-button
        v-else-if="activity.status === 'CANCELLED'"
        size="small"
        class="action-btn cancelled-btn"
        disabled
      >
        <el-icon><CircleClose /></el-icon>
        已取消
      </el-button>

      <!-- 其他不可报名情况 -->
      <el-button
        v-else
        size="small"
        class="action-btn disabled-btn"
        disabled
      >
        <el-icon><Lock /></el-icon>
        无法报名
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  Picture,
  Clock,
  Location,
  User,
  Plus,
  Check,
  Lock,
  Timer,
  CircleCheck,
  CircleClose,
  Close
} from '@element-plus/icons-vue'
import {
  formatDateTimeValue,
  getStatusText,
  getStatusType,
  getActivityTypeText, formatDisplayDateTime
} from '@/utils/format'
import UserAvatar from '@/components/common/UserAvatar.vue'
import type { ActivityBriefResponse } from '@/types/activity'
import { useAuth } from '@/composables/useAuth.ts'

interface Props {
  activity: ActivityBriefResponse
}

interface Emits {
  (e: 'register', activityId: number): void
  (e: 'unregister', activityId: number): void
  (e: 'view-detail', activityId: number): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
const { currentUser } = useAuth()

const registering = ref(false)
const unregistering = ref(false)

const canRegister = computed(() => {
  return props.activity.status === 'RECRUITING' &&
    props.activity.currentParticipants < props.activity.maxParticipants &&
    new Date(props.activity.registrationDeadline) > new Date()
})

const canUnregister = computed(() => {
  if (!props.activity.isRegistered) return false

  // 创建者不能退出自己创建的活动
  if (props.activity.creator.id === currentUser?.value?.id) return false

  // 检查是否还在报名期内
  const now = new Date()
  const deadline = new Date(props.activity.registrationDeadline)
  if (now > deadline) return false

  // 检查活动状态
  const allowedStatuses = ['RECRUITING']
  return allowedStatuses.includes(props.activity.status)
})

const handleRegister = async () => {
  registering.value = true
  try {
    emit('register', props.activity.id)
  } finally {
    setTimeout(() => {
      registering.value = false
    }, 1000)
  }
}

const handleUnregister = async () => {
  unregistering.value = true
  try {
    emit('unregister', props.activity.id)
  } finally {
    setTimeout(() => {
      unregistering.value = false
    }, 1000)
  }
}

const handleViewDetail = () => {
  emit('view-detail', props.activity.id)
}
</script>

<style scoped>
.activity-card {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid var(--border-color-light);
}

.activity-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-hover);
  border-color: var(--primary-color);
}

.card-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.activity-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.activity-card:hover .activity-image {
  transform: scale(1.05);
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--bg-color-secondary) 0%, var(--border-color-light) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-tertiary);
}

.placeholder-icon {
  font-size: var(--font-size-3xl);
}

.status-badge {
  position: absolute;
  top: var(--spacing-md);
  left: var(--spacing-md);
  z-index: 2;
}

.status-tag {
  backdrop-filter: blur(10px);
  border: none;
  font-weight: var(--font-weight-medium);
}

.card-content {
  padding: var(--spacing-lg);
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-md);
  gap: var(--spacing-md);
}

.activity-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
  margin: 0;
  line-height: 1.4;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  flex: 1;
}

.activity-type {
  flex-shrink: 0;
}

.activity-meta {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-sm);
  color: var(--text-color-secondary);
}

.meta-icon {
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
  flex-shrink: 0;
}

.creator-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-color-light);
}

.creator-name {
  font-size: var(--font-size-sm);
  color: var(--text-color-secondary);
  font-weight: var(--font-weight-medium);
}

/* 调整卡片操作区域布局，当有多个按钮时 */
.card-actions {
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-color-secondary);
  display: flex;
  gap: var(--spacing-sm);
  border-top: 1px solid var(--border-color-light);
  flex-wrap: wrap; /* 允许按钮换行 */
}

.action-btn {
  flex: 1;
  min-width: 80px; /* 设置最小宽度 */
  border-radius: var(--border-radius-md);
  font-weight: var(--font-weight-medium);
  transition: var(--transition-base);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
}

.register-btn {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.register-btn:hover {
  background: var(--primary-light);
  border-color: var(--primary-light);
  transform: translateY(-1px);
}

.registered-btn {
  background: var(--success-color);
  border-color: var(--success-color);
  color: white;
}

.unregister-btn {
  background: var(--warning-color);
  border-color: var(--warning-color);
  color: white;
}

.unregister-btn:hover {
  background: var(--warning-light);
  border-color: var(--warning-light);
  transform: translateY(-1px);
}

.full-btn {
  background: var(--warning-color);
  border-color: var(--warning-color);
  color: white;
}

.closed-btn {
  background: var(--danger-color);
  border-color: var(--danger-color);
  color: white;
}

.progress-btn {
  background: var(--info-color);
  border-color: var(--info-color);
  color: white;
}

.completed-btn {
  background: var(--success-color);
  border-color: var(--success-color);
  color: white;
  opacity: 0.8;
}

.cancelled-btn {
  background: var(--text-color-tertiary);
  border-color: var(--text-color-tertiary);
  color: white;
}

.disabled-btn {
  background: var(--bg-color-secondary);
  color: var(--text-color-tertiary);
  border-color: var(--border-color);
}

.detail-btn {
  background: transparent;
  color: var(--text-color-secondary);
  border-color: var(--border-color);
}

.detail-btn:hover {
  color: var(--primary-color);
  border-color: var(--primary-color);
  background: rgba(64, 158, 255, 0.05);
}

@media (max-width: 768px) {
  .card-image {
    height: 160px;
  }

  .card-content {
    padding: var(--spacing-md);
  }

  .activity-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }

  .card-actions {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
    min-width: auto;
  }
}
</style>
