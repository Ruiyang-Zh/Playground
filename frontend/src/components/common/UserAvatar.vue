<template>
  <div
    class="user-avatar-wrapper"
    :class="[
      `size-${size}`,
      { 'clickable': clickable, 'show-hover': showHover }
    ]"
    @click="handleClick"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
    ref="avatarRef"
  >
    <div class="avatar-container">
      <img
        v-if="user?.avatar"
        :src="user.avatar"
        :alt="user.username || '用户头像'"
        class="avatar-image"
        @error="handleImageError"
      />
      <div v-else class="avatar-placeholder">
        <el-icon class="placeholder-icon"><User /></el-icon>
      </div>

      <!-- 在线状态指示器 -->
      <div v-if="showOnlineStatus" class="online-indicator"></div>
    </div>

    <!-- 用户信息卡片 -->
    <UserCard
      v-if="showCard && user"
      :user="user"
      :visible="showCard"
      :position="cardPosition"
      @close="handleCloseCard"
      @mouseenter="handleCardMouseEnter"
      @mouseleave="handleCardMouseLeave"
      class="user-card"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import { User } from '@element-plus/icons-vue'
import UserCard from './UserCard.vue'
import { useUserInfo } from '@/composables/useUserInfo'
import type { UserPublicInfoResponse } from '@/types/user'

interface Props {
  user?: UserPublicInfoResponse | null
  userId?: number
  size?: 'small' | 'medium' | 'large' | 'extra-large'
  clickable?: boolean
  showHover?: boolean
  showOnlineStatus?: boolean
}

interface Emits {
  (e: 'click', user: UserPublicInfoResponse): void
}

const props = withDefaults(defineProps<Props>(), {
  size: 'medium',
  clickable: true,
  showHover: true,
  showOnlineStatus: false
})

const emit = defineEmits<Emits>()

const { fetchUserInfo } = useUserInfo()
const showCard = ref(false)
const cardPosition = ref({ x: 0, y: 0 })
const hoverTimer = ref<number>()
const isHoveringAvatar = ref(false)
const isHoveringCard = ref(false)
const avatarRef = ref<HTMLElement>()

const displayUser = computed(() => {
  return props.user || null
})

const handleClick = async () => {
  if (!props.clickable) return

  let user = displayUser.value
  if (!user && props.userId) {
    try {
      user = await fetchUserInfo(props.userId)
    } catch (error) {
      console.error('Failed to fetch user info:', error)
      return
    }
  }

  if (user) {
    emit('click', user)
  }
}

const handleMouseEnter = async (event: MouseEvent) => {
  if (!props.showHover) return

  isHoveringAvatar.value = true
  clearTimeout(hoverTimer.value)

  hoverTimer.value = window.setTimeout(async () => {
    if (!isHoveringAvatar.value) return

    let user = displayUser.value
    if (!user && props.userId) {
      try {
        user = await fetchUserInfo(props.userId)
      } catch (error) {
        console.error('Failed to fetch user info:', error)
        return
      }
    }

    if (user && avatarRef.value) {
      const rect = avatarRef.value.getBoundingClientRect()
      cardPosition.value = {
        x: rect.left + rect.width / 2,
        y: rect.top
      }
      showCard.value = true
    }
  }, 300)
}

const handleMouseLeave = () => {
  isHoveringAvatar.value = false
  clearTimeout(hoverTimer.value)

  // 延迟关闭，给用户时间移动到卡片上
  setTimeout(() => {
    if (!isHoveringAvatar.value && !isHoveringCard.value) {
      showCard.value = false
    }
  }, 150)
}

const handleCardMouseEnter = () => {
  isHoveringCard.value = true
}

const handleCardMouseLeave = () => {
  isHoveringCard.value = false
  setTimeout(() => {
    if (!isHoveringAvatar.value && !isHoveringCard.value) {
      showCard.value = false
    }
  }, 150)
}

const handleCloseCard = () => {
  showCard.value = false
  isHoveringCard.value = false
  isHoveringAvatar.value = false
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}
</script>

<style scoped>
.user-avatar-wrapper {
  position: relative;
  display: inline-block;
}

.avatar-container {
  position: relative;
  border-radius: 50%;
  overflow: hidden;
  transition: var(--transition-base);
}

.clickable .avatar-container {
  cursor: pointer;
}

.clickable .avatar-container:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: var(--bg-color-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-tertiary);
  border-radius: 50%;
}

.online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: var(--success-color);
  border: 2px solid var(--bg-color-primary);
  border-radius: 50%;
}

/* 尺寸样式 */
.size-small .avatar-container {
  width: 32px;
  height: 32px;
}

.size-small .placeholder-icon {
  font-size: 16px;
}

.size-small .online-indicator {
  width: 8px;
  height: 8px;
  bottom: 0;
  right: 0;
}

.size-medium .avatar-container {
  width: 40px;
  height: 40px;
}

.size-medium .placeholder-icon {
  font-size: 20px;
}

.size-large .avatar-container {
  width: 56px;
  height: 56px;
}

.size-large .placeholder-icon {
  font-size: 28px;
}

.size-extra-large .avatar-container {
  width: 80px;
  height: 80px;
}

.size-extra-large .placeholder-icon {
  font-size: 40px;
}

.size-extra-large .online-indicator {
  width: 16px;
  height: 16px;
  bottom: 4px;
  right: 4px;
}
</style>
