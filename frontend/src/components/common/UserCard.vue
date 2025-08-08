<template>
  <Teleport to="body">
    <Transition name="user-card">
      <div
        v-if="visible && user"
        class="user-card-container"
        :style="cardStyle"
        @mouseenter="$emit('mouseenter')"
        @mouseleave="$emit('mouseleave')"
        @click.stop
      >
        <div class="card-header">
          <UserAvatar
            :user="user"
            size="large"
            :clickable="false"
            :show-hover="false"
            class="card-avatar"
          />
          <div class="user-basic-info">
            <h4 class="username">{{ user.username }}</h4>
            <p class="user-description">
              {{ user.description || '这个人很懒，什么都没有留下' }}
            </p>
          </div>
        </div>

        <div v-if="user.sportsPreference?.length" class="user-sports">
          <div class="sports-label">运动偏好</div>
          <div class="sports-tags">
            <el-tag
              v-for="sport in user.sportsPreference.slice(0, 3)"
              :key="sport"
              size="small"
              type="info"
              class="sport-tag"
            >
              {{ getActivityTypeText(sport) }}
            </el-tag>
            <span v-if="user.sportsPreference.length > 3" class="more-sports">
              +{{ user.sportsPreference.length - 3 }}
            </span>
          </div>
        </div>

        <div class="card-actions">
          <el-button size="small" type="primary" class="action-btn" @click="handleSendMessage">
            <el-icon><Message /></el-icon>
            私信
          </el-button>
          <el-button size="small" class="action-btn" @click="handleViewProfile">
            <el-icon><UserFilled /></el-icon>
            查看资料
          </el-button>
        </div>

        <!-- 关闭按钮 -->
        <button class="close-btn" @click="$emit('close')">
          <el-icon><Close /></el-icon>
        </button>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Message, UserFilled, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import UserAvatar from './UserAvatar.vue'
import { getActivityTypeText } from '@/utils/format'
import type { UserPublicInfoResponse } from '@/types/user'

interface Props {
  user: UserPublicInfoResponse
  visible: boolean
  position: { x: number; y: number }
}

interface Emits {
  (e: 'close'): void
  (e: 'mouseenter'): void
  (e: 'mouseleave'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
const router = useRouter()

const cardStyle = computed(() => {
  const { x, y } = props.position
  const cardWidth = 280
  const cardHeight = 300 // 估算卡片高度
  const padding = 20

  // 获取视窗尺寸
  const viewportWidth = window.innerWidth
  const viewportHeight = window.innerHeight

  let finalX = x
  let finalY = y - 10
  let transformX = '-50%'
  let transformY = '-100%'

  // 水平位置调整
  if (x - cardWidth / 2 < padding) {
    // 左边界溢出，调整到左对齐
    finalX = padding
    transformX = '0%'
  } else if (x + cardWidth / 2 > viewportWidth - padding) {
    // 右边界溢出，调整到右对齐
    finalX = viewportWidth - padding
    transformX = '-100%'
  }

  // 垂直位置调整
  if (y - cardHeight < padding) {
    // 上边界溢出，显示在下方
    finalY = y + 10
    transformY = '0%'
  } else if (finalY - cardHeight < 0) {
    // 确保不会超出顶部
    finalY = cardHeight + padding
    transformY = '-100%'
  }

  return {
    left: `${finalX}px`,
    top: `${finalY}px`,
    transform: `translateX(${transformX}) translateY(${transformY})`
  }
})

// 私信功能
const handleSendMessage = () => {
  ElMessage.info('私信功能开发中，敬请期待！')
}

// 查看个人资料
const handleViewProfile = () => {
  router.push(`/users/${props.user.id}`)
  emit('close')
}
</script>

<style scoped>
.user-card-container {
  position: fixed;
  width: 280px;
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  border: 1px solid var(--border-color-light);
  box-shadow: var(--shadow-xl);
  padding: var(--spacing-lg);
  z-index: 2001;
}

.close-btn {
  position: absolute;
  top: var(--spacing-sm);
  right: var(--spacing-sm);
  width: 24px;
  height: 24px;
  border: none;
  background: var(--bg-color-secondary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-color-tertiary);
  transition: var(--transition-base);
}

.close-btn:hover {
  background: var(--border-color);
  color: var(--text-color-secondary);
}

.card-header {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.user-basic-info {
  flex: 1;
  min-width: 0;
}

.username {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
  margin: 0 0 var(--spacing-xs) 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-description {
  font-size: var(--font-size-sm);
  color: var(--text-color-secondary);
  margin: 0;
  line-height: 1.4;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.user-sports {
  margin-bottom: var(--spacing-lg);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-color-light);
}

.sports-label {
  font-size: var(--font-size-xs);
  color: var(--text-color-tertiary);
  margin-bottom: var(--spacing-sm);
  font-weight: var(--font-weight-medium);
}

.sports-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  align-items: center;
}

.sport-tag {
  border-radius: var(--border-radius-full);
  font-size: var(--font-size-xs);
}

.more-sports {
  font-size: var(--font-size-xs);
  color: var(--text-color-tertiary);
  font-weight: var(--font-weight-medium);
}

.card-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.action-btn {
  flex: 1;
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-xs);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
}

.user-card-enter-active,
.user-card-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-card-enter-from,
.user-card-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-100%) scale(0.95);
}
</style>
