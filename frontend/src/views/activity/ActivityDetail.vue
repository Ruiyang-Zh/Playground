<template>
  <div class="activity-detail-page">
    <!-- 主内容区域 -->
    <div class="main-content">
      <div v-if="activity" class="activity-content">
        <!-- 活动标题、标签、时间地点和图片 -->
        <div class="activity-header-card">
          <div class="header-left">
            <!-- 标题和标签 -->
            <div class="title-section">
              <h1 class="activity-title">{{ activity.title }}</h1>
              <div class="header-tags">
                <el-tag type="info" size="large" class="type-tag">
                  {{ getActivityTypeText(activity.type) }}
                </el-tag>
                <el-tag
                  :type="getStatusType(activity.status)"
                  size="large"
                  class="status-tag"
                >
                  {{ getStatusText(activity.status) }}
                </el-tag>
              </div>
            </div>

            <!-- 时间信息 -->
            <div class="time-section">
              <el-icon class="section-icon"><Clock /></el-icon>
              <div class="time-content">
                <span class="section-label">活动时间</span>
                <div class="time-details">
                  <div class="time-item">
                    <span>开始：{{ formatDisplayDateTime(activity.startTime) }}</span>
                  </div>
                  <div class="time-item">
                    <span>结束：{{ formatDisplayDateTime(activity.endTime) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 地点信息 -->
            <div class="location-section">
              <el-icon class="section-icon"><Location /></el-icon>
              <div class="location-content">
                <span class="section-label">活动地点</span>
                <span class="location-text">{{ getFullAddress(activity) }}</span>
              </div>
            </div>
          </div>

          <!-- 图片展示区域 -->
          <div class="header-right">
            <div class="images-container">
              <div v-if="activity.images?.length" class="images-display">
                <div class="image-viewer">
                  <img
                    :src="activity.images[currentImageIndex]"
                    :alt="`活动图片 ${currentImageIndex + 1}`"
                    class="main-image"
                    @click="handleImagePreview(activity.images[currentImageIndex], currentImageIndex)"
                  />

                  <!-- 图片导航 -->
                  <div v-if="activity.images.length > 1" class="image-navigation">
                    <button
                      @click="previousImage"
                      class="nav-btn prev-btn"
                      :disabled="currentImageIndex === 0"
                    >
                      <el-icon><ArrowLeft /></el-icon>
                    </button>

                    <div class="image-indicators">
                      <span
                        v-for="(image, index) in activity.images"
                        :key="index"
                        :class="['indicator', { active: index === currentImageIndex }]"
                        @click="currentImageIndex = index"
                      ></span>
                    </div>

                    <button
                      @click="nextImage"
                      class="nav-btn next-btn"
                      :disabled="currentImageIndex === activity.images.length - 1"
                    >
                      <el-icon><ArrowRight /></el-icon>
                    </button>
                  </div>
                </div>
              </div>

              <!-- 无图片占位符 -->
              <div v-else class="no-images-placeholder">
                <el-icon class="placeholder-icon"><Picture /></el-icon>
                <span class="placeholder-text">暂无活动图片</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 活动详情卡片 -->
        <div class="activity-details-card">
          <h3 class="card-title">
            <el-icon><Document /></el-icon>
            活动详情
          </h3>

          <div class="details-content">
            <!-- 活动描述 -->
            <div class="description-section">
              <h4 class="subsection-title">活动描述</h4>
              <div class="description-scroll-area">
                <p class="description-text">{{ activity.description || '暂无详细描述' }}</p>
              </div>
            </div>

            <!-- 注意事项 -->
            <div class="requirements-section">
              <h4 class="subsection-title">注意事项</h4>
              <div class="requirements-scroll-area">
                <div v-if="activity.requirements" class="requirements-content">
                  <p class="requirements-text">{{ activity.requirements }}</p>
                </div>
                <div v-else class="no-requirements">
                  <span>暂无特别注意事项</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 下滑提示 -->
        <div class="scroll-indicator" @click="scrollToComments">
          <el-icon class="scroll-arrow"><ArrowDown /></el-icon>
          <span class="scroll-text">查看讨论区</span>
        </div>
      </div>

      <!-- 加载和错误状态 -->
      <div v-else-if="loading" class="loading-state">
        <el-skeleton animated>
          <template #template>
            <div class="skeleton-content">
              <el-skeleton-item variant="h1" style="width: 70%; height: 48px;" />
              <el-skeleton-item variant="text" style="width: 200px; height: 32px; margin-top: 16px;" />
              <el-skeleton-item variant="text" style="width: 100%; height: 120px; margin-top: 32px;" />
              <el-skeleton-item variant="image" style="width: 100%; height: 200px; margin-top: 32px;" />
            </div>
          </template>
        </el-skeleton>
      </div>

      <div v-else class="error-state">
        <el-icon class="error-icon"><Warning /></el-icon>
        <h3 class="error-title">活动不存在或已被删除</h3>
        <p class="error-text">很抱歉，您要查看的活动可能不存在或已被删除。</p>
        <el-button type="primary" @click="$router.push('/')">
          返回首页
        </el-button>
      </div>

      <!-- 评论区 -->
      <ActivityComments
        v-if="activity"
        :key="activity.id"
        :activity-id="activity.id"
        class="comments-section"
        ref="commentsRef"
      />
    </div>

    <!-- 悬浮右侧参与信息栏 -->
    <div v-if="activity" class="participation-sidebar">
      <div class="sidebar-content">
        <!-- 发起者信息 -->
        <div class="organizer-section">
          <h4 class="section-title">活动组织者</h4>
          <div class="organizer-info">
            <UserAvatar
              :user="activity.creator"
              size="large"
              class="organizer-avatar"
            />
            <div class="organizer-details">
              <span class="organizer-name">{{ activity.creator.username }}</span>
              <span class="organizer-label">组织者</span>
            </div>
          </div>

          <div v-if="activity.contactInfo" class="contact-info">
            <el-icon class="contact-icon"><Phone /></el-icon>
            <span class="contact-text">{{ activity.contactInfo }}</span>
          </div>

          <!-- 编辑按钮 -->
          <el-button
            v-if="activity.canUpdate"
            type="primary"
            size="small"
            @click="handleEdit"
            class="edit-btn"
          >
            <el-icon><Edit /></el-icon>
            编辑活动
          </el-button>
        </div>

        <!-- 报名截止信息 -->
        <div class="deadline-section">
          <h4 class="section-title">报名信息</h4>
          <div class="deadline-info">
            <div class="deadline-item">
              <el-icon class="deadline-icon"><Calendar /></el-icon>
              <div class="deadline-content">
                <span class="deadline-label">报名截止</span>
                <span class="deadline-value">{{ formatDisplayDateTime(activity.registrationDeadline)
                  }}</span>
              </div>
            </div>
            <div class="deadline-item">
              <el-icon class="deadline-icon"><User /></el-icon>
              <div class="deadline-content">
                <span class="deadline-label">参与人数</span>
                <span class="deadline-value">
                  {{ activity.currentParticipants }}/{{ activity.maxParticipants }}人
                  <span v-if="activity.minParticipants > 0" class="min-participants">
                    (最少{{ activity.minParticipants }}人)
                  </span>
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 参与者列表 -->
        <div class="participants-section">
          <h4 class="section-title">参与者 ({{ activity.currentParticipants }})</h4>
          <div class="participants-list-container">
            <div v-if="activity.participants?.length" class="participants-list">
              <div
                v-for="participant in activity.participants"
                :key="participant.id"
                class="participant-item"
              >
                <UserAvatar
                  :user="participant"
                  size="small"
                  class="participant-avatar"
                />
                <span class="participant-name">{{ participant.username }}</span>
              </div>
            </div>
            <div v-else class="no-participants">
              <el-icon class="empty-icon"><User /></el-icon>
              <span>暂无参与者</span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button
            v-if="!currentUser || activity.canRegister && !activity.isRegistered"
            type="primary"
            size="large"
            @click="handleRegister"
            :loading="registering"
            class="action-btn register-btn"
          >
            <el-icon><Plus /></el-icon>
            立即报名
          </el-button>

          <!-- 已报名区域 - 使用横向布局 -->
          <div v-else-if="activity.isRegistered" class="registered-actions">
            <el-button
              type="success"
              size="large"
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
              size="large"
              @click="handleUnregister"
              :loading="unregistering"
              class="action-btn unregister-btn"
            >
              <el-icon><Close /></el-icon>
              取消报名
            </el-button>
          </div>

          <el-button
            v-else
            size="large"
            class="action-btn disabled-btn"
            disabled
          >
            <el-icon><Lock /></el-icon>
            {{ getDisabledText() }}
          </el-button>

          <el-button
            v-if="activity.canCancel"
            type="danger"
            size="large"
            @click="handleCancel"
            class="action-btn cancel-btn"
          >
            <el-icon><Delete /></el-icon>
            取消活动
          </el-button>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showImageViewer"
      :url-list="activity?.images || []"
      :initial-index="previewImageIndex"
      @close="showImageViewer = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Clock,
  Calendar,
  Location,
  User,
  Picture,
  Document,
  Warning,
  Edit,
  Plus,
  Check,
  Lock,
  Delete,
  ArrowDown,
  ArrowLeft,
  ArrowRight,
  Phone,
  Close
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useActivity } from '@/composables/useActivity'
import UserAvatar from '@/components/common/UserAvatar.vue'
import ActivityComments from '@/components/activity/ActivityComments.vue'
import {
  formatDateTimeValue, formatDisplayDateTime,
  getActivityTypeText,
  getStatusText,
  getStatusType
} from '@/utils/format'
import { useAuth } from '@/composables/useAuth.ts'

const route = useRoute()
const router = useRouter()
const { currentUser } = useAuth()
const { currentActivity: activity, loading, registerActivity, unregisterActivity, cancelActivity, fetchActivityDetail } = useActivity()

const registering = ref(false)
const unregistering = ref(false)
const showImageViewer = ref(false)
const currentImageIndex = ref(0)
const previewImageIndex = ref(0)
const commentsRef = ref()

const activityId = computed(() => Number(route.params.id))

const getFullAddress = (activity: any) => {
  if (!activity) return '地点待定'
  const parts = [activity.province, activity.city, activity.district, activity.venue]
  const filteredParts = parts.filter(part => part && part.trim())
  return filteredParts.length > 0 ? filteredParts.join(' ') : '地点待定'
}

const getDisabledText = () => {
  if (!activity.value) return '无法报名'

  switch (activity.value.status) {
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

const scrollToComments = () => {
  nextTick(() => {
    commentsRef.value?.$el?.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    })
  })
}

const previousImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--
  }
}

const nextImage = () => {
  if (activity.value?.images && currentImageIndex.value < activity.value.images.length - 1) {
    currentImageIndex.value++
  }
}

const handleRegister = async () => {
  try {
    if (!currentUser.value) {
      ElMessage.warning('请先登录')
      return
    }
    registering.value = true
    await registerActivity(activityId.value)
    await fetchActivityDetail(activityId.value)
  } catch (error) {
    console.error('Register failed:', error)
  } finally {
    registering.value = false
  }
}

const handleEdit = () => {
  router.push(`/activities/${activityId.value}/edit`)
}

const canUnregister = computed(() => {
  if (!activity.value || !activity.value.isRegistered) return false

  // 创建者不能退出自己创建的活动
  if (activity.value.creator.id === currentUser?.value?.id) return false

  // 检查是否还在报名期内
  const now = new Date()
  const deadline = new Date(activity.value.registrationDeadline)
  if (now > deadline) return false

  // 检查活动状态
  const allowedStatuses = ['RECRUITING', 'FULL']
  return allowedStatuses.includes(activity.value.status)
})

const handleUnregister = async () => {
  try {
    unregistering.value = true
    await unregisterActivity(activityId.value)
    await fetchActivityDetail(activityId.value)
  } catch (error) {
    console.error('Unregister failed:', error)
  } finally {
    unregistering.value = false
  }
}

const handleCancel = async () => {
  try {
    await cancelActivity(activityId.value)
    ElMessage.success('活动已取消')
    await fetchActivityDetail(activityId.value)
  } catch (error) {
    console.error('Cancel activity failed:', error)
  }
}

const handleImagePreview = (image: string, index: number) => {
  previewImageIndex.value = index
  showImageViewer.value = true
}

onMounted(() => {
  fetchActivityDetail(activityId.value)
})
</script>

<style scoped>
.activity-detail-page {
  display: flex;
  min-height: 100vh;
  background: var(--bg-color-secondary);
  padding-top: var(--header-height);
}

.main-content {
  flex: 1;
  max-width: calc(100% - 340px);
  padding: var(--spacing-xl) var(--spacing-xl) var(--spacing-xl) var(--spacing-2xl);
}

.activity-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

/* 活动头部卡片 - 包含标题、时间地点、图片 */
.activity-header-card {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  border: 1px solid var(--border-color-light);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-2xl);
  display: flex;
  gap: var(--spacing-2xl);
  min-height: 280px;
}

.header-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  min-width: 0;
}

.title-section {
  flex-shrink: 0;
}

.activity-title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-extrabold);
  color: var(--text-color-primary);
  margin: 0 0 var(--spacing-lg) 0;
  line-height: 1.2;
  word-wrap: break-word;
}

.header-tags {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.type-tag,
.status-tag {
  font-weight: var(--font-weight-semibold);
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--border-radius-full);
}

.time-section,
.location-section {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
  flex-shrink: 0;
}

.section-icon {
  font-size: var(--font-size-lg);
  color: var(--primary-color);
  margin-top: 2px;
  flex-shrink: 0;
}

.time-content,
.location-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  min-width: 0;
}

.section-label {
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
  font-weight: var(--font-weight-medium);
}

.time-details {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.time-item {
  font-size: var(--font-size-base);
  color: var(--text-color-primary);
  font-weight: var(--font-weight-semibold);
}

.location-text {
  font-size: var(--font-size-base);
  color: var(--text-color-primary);
  font-weight: var(--font-weight-semibold);
  word-wrap: break-word;
}

/* 图片展示区域 */
.header-right {
  width: 320px;
  flex-shrink: 0;
}

.images-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.images-display {
  width: 100%;
  height: 100%;
  position: relative;
  min-height: 240px;
}

.image-viewer {
  width: 100%;
  height: 100%;
  position: relative;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  background: var(--bg-color-secondary);
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  transition: var(--transition-base);
}

.main-image:hover {
  transform: scale(1.02);
}

.image-navigation {
  position: absolute;
  bottom: var(--spacing-md);
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  background: rgba(0, 0, 0, 0.7);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-full);
  backdrop-filter: blur(10px);
}

.nav-btn {
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  padding: var(--spacing-xs);
  border-radius: var(--border-radius-md);
  transition: var(--transition-base);
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
}

.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.image-indicators {
  display: flex;
  gap: var(--spacing-xs);
}

.indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: var(--transition-base);
}

.indicator.active {
  background: white;
}

.no-images-placeholder {
  width: 100%;
  height: 100%;
  min-height: 240px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-color-tertiary);
  background: var(--bg-color-secondary);
  border-radius: var(--border-radius-lg);
  border: 2px dashed var(--border-color);
}

.placeholder-icon {
  font-size: var(--font-size-4xl);
  margin-bottom: var(--spacing-sm);
}

.placeholder-text {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

/* 活动详情卡片 */
.activity-details-card {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  border: 1px solid var(--border-color-light);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-2xl);
  min-height: 300px;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
  margin: 0 0 var(--spacing-lg) 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-shrink: 0;
}

.details-content {
  flex: 1;
  display: flex;
  gap: var(--spacing-2xl);
  min-height: 0;
}

.description-section,
.requirements-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.subsection-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
  margin: 0 0 var(--spacing-md) 0;
  flex-shrink: 0;
}

.description-scroll-area,
.requirements-scroll-area {
  flex: 1;
  overflow-y: auto;
  padding-right: var(--spacing-sm);
  min-height: 200px;
}

.description-text {
  font-size: var(--font-size-base);
  color: var(--text-color-primary);
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
}

.requirements-content {
  background: rgba(245, 108, 108, 0.1);
  border-left: 4px solid var(--warning-color);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
}

.requirements-text {
  font-size: var(--font-size-base);
  color: var(--text-color-primary);
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
}

.no-requirements {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-color-tertiary);
  font-size: var(--font-size-base);
}

.scroll-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  transition: var(--transition-base);
  padding: var(--spacing-lg);
  color: var(--text-color-secondary);
  margin: var(--spacing-md) 0;
}

.scroll-indicator:hover {
  color: var(--primary-color);
  transform: translateY(-2px);
}

.scroll-arrow {
  font-size: var(--font-size-xl);
  animation: bounce 2s infinite;
}

.scroll-text {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-8px);
  }
  60% {
    transform: translateY(-4px);
  }
}

/* 悬浮右侧参与信息栏 */
.participation-sidebar {
  position: fixed;
  top: var(--header-height);
  right: var(--spacing-xl);
  width: 300px;
  height: calc(100vh - var(--header-height) - var(--spacing-2xl));
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  border: 1px solid var(--border-color-light);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  z-index: 100;
}

.sidebar-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.organizer-section,
.deadline-section {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--border-color-light);
  flex-shrink: 0;
}

.section-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
  margin: 0 0 var(--spacing-md) 0;
}

.organizer-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.organizer-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.organizer-name {
  font-size: var(--font-size-base);
  color: var(--text-color-primary);
  font-weight: var(--font-weight-semibold);
}

.organizer-label {
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
}

.edit-btn {
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  width: 100%;
  justify-content: center;
}

.deadline-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.deadline-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
}

.deadline-icon {
  font-size: var(--font-size-base);
  color: var(--primary-color);
  margin-top: 2px;
}

.deadline-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.deadline-label {
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
  font-weight: var(--font-weight-medium);
}

.deadline-value {
  font-size: var(--font-size-sm);
  color: var(--text-color-primary);
  font-weight: var(--font-weight-semibold);
  line-height: 1.4;
}

.min-participants {
  font-size: var(--font-size-xs);
  color: var(--text-color-secondary);
  font-weight: var(--font-weight-normal);
}

.participants-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: var(--spacing-lg) var(--spacing-xl) 0;
  min-height: 0;
}

.participants-list-container {
  flex: 1;
  min-height: 0;
}

.participants-list {
  height: 100%;
  overflow-y: auto;
  padding-right: var(--spacing-sm);
}

.participant-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-md);
  transition: var(--transition-base);
  cursor: pointer;
  margin-bottom: var(--spacing-xs);
}

.participant-item:hover {
  background: var(--bg-color-secondary);
}

.contact-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-color-secondary);
  border-radius: var(--border-radius-md);
}

.contact-icon {
  font-size: var(--font-size-base);
  flex-shrink: 0;
}

.contact-text {
  font-size: var(--font-size-sm);
  color: var(--text-color-primary);
  font-weight: var(--font-weight-medium);
  word-break: break-all;
  line-height: 1.4;
}

.participant-name {
  font-size: var(--font-size-sm);
  color: var(--text-color-primary);
  font-weight: var(--font-weight-medium);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.no-participants {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl);
  color: var(--text-color-tertiary);
  text-align: center;
  height: 100%;
}

.empty-icon {
  font-size: var(--font-size-2xl);
  margin-bottom: var(--spacing-sm);
}

/* 修改操作按钮区域的样式 */
.action-buttons {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-top: 1px solid var(--border-color-light);
  background: var(--bg-color-secondary);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  flex-shrink: 0;
}

/* 当有已报名和取消报名按钮时，使用横向布局 */
.registered-actions {
  display: flex;
  flex-direction: row;
  gap: var(--spacing-sm);
}

.registered-actions .action-btn {
  flex: 1;
}

.register-btn {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.register-btn:hover {
  background: var(--primary-light);
  border-color: var(--primary-light);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
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

.unregister-btn {
  background: var(--warning-color);
  border-color: var(--warning-color);
  color: white;
}

.unregister-btn:hover {
  background: var(--warning-light);
  border-color: var(--warning-light);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
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

.comments-section {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  border: 1px solid var(--border-color-light);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-xl);
  margin-top: var(--spacing-xl);
}

.loading-state,
.error-state {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  border: 1px solid var(--border-color-light);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-2xl);
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-state {
  flex-direction: column;
  text-align: center;
  padding: var(--spacing-3xl);
}

.error-icon {
  font-size: var(--font-size-5xl);
  color: var(--text-color-tertiary);
  margin-bottom: var(--spacing-lg);
}

.error-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-secondary);
  margin: 0 0 var(--spacing-md) 0;
}

.error-text {
  font-size: var(--font-size-base);
  color: var(--text-color-tertiary);
  margin: 0 0 var(--spacing-xl) 0;
}

/* 滚动条样式 */
.description-scroll-area::-webkit-scrollbar,
.requirements-scroll-area::-webkit-scrollbar,
.participants-list::-webkit-scrollbar {
  width: 4px;
}

.description-scroll-area::-webkit-scrollbar-track,
.requirements-scroll-area::-webkit-scrollbar-track,
.participants-list::-webkit-scrollbar-track {
  background: var(--bg-color-secondary);
}

.description-scroll-area::-webkit-scrollbar-thumb,
.requirements-scroll-area::-webkit-scrollbar-thumb,
.participants-list::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 2px;
}

.description-scroll-area::-webkit-scrollbar-thumb:hover,
.requirements-scroll-area::-webkit-scrollbar-thumb:hover,
.participants-list::-webkit-scrollbar-thumb:hover {
  background: var(--text-color-tertiary);
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .participation-sidebar {
    width: 280px;
  }

  .main-content {
    max-width: calc(100% - 320px);
  }

  .header-right {
    width: 280px;
  }
}

@media (max-width: 1200px) {
  .main-content {
    max-width: 100%;
    padding-right: var(--spacing-xl);
  }

  .participation-sidebar {
    position: relative;
    top: 0;
    right: 0;
    width: 100%;
    height: auto;
    margin-top: var(--spacing-xl);
    position: static;
  }

  .activity-detail-page {
    flex-direction: column;
  }

  .activity-header-card {
    min-height: auto;
    flex-direction: column;
    gap: var(--spacing-xl);
  }

  .header-right {
    width: 100%;
    height: 200px;
  }

  .activity-details-card {
    min-height: auto;
  }

  .details-content {
    flex-direction: column;
    gap: var(--spacing-lg);
  }

  .description-scroll-area,
  .requirements-scroll-area {
    min-height: 120px;
    flex-shrink: 0;
  }
}

@media (max-width: 768px) {
  .activity-detail-page {
    padding-top: calc(var(--header-height) + var(--spacing-md));
  }

  .main-content {
    padding: var(--spacing-md);
  }

  .activity-header-card {
    padding: var(--spacing-lg);
    gap: var(--spacing-lg);
  }

  .activity-title {
    font-size: var(--font-size-2xl);
  }

  .header-tags {
    flex-wrap: wrap;
    gap: var(--spacing-sm);
  }

  .header-right {
    height: 180px;
  }

  .activity-details-card {
    padding: var(--spacing-lg);
  }

  .details-content {
    gap: var(--spacing-md);
  }

  .organizer-section,
  .deadline-section,
  .participants-section,
  .action-buttons {
    padding: var(--spacing-md) var(--spacing-lg);
  }
}

@media (max-width: 480px) {
  .main-content {
    padding: var(--spacing-sm);
  }

  .activity-header-card {
    padding: var(--spacing-md);
  }

  .activity-details-card {
    padding: var(--spacing-md);
  }

  .activity-title {
    font-size: var(--font-size-xl);
  }

  .header-right {
    height: 160px;
  }

  .details-content {
    gap: var(--spacing-sm);
  }

  .description-scroll-area,
  .requirements-scroll-area {
    min-height: 100px;
  }
}

</style>
