<template>
  <!-- 遮罩层 -->
  <Transition name="overlay">
    <div
      v-show="visible"
      class="panel-overlay"
      @click="$emit('close')"
    ></div>
  </Transition>

  <!-- 侧边面板 -->
  <Transition name="slide-left">
    <div v-show="visible" class="my-activities-panel">
      <div class="panel-header">
        <h3 class="panel-title">我的活动</h3>
        <el-button
          text
          @click="$emit('close')"
          class="close-btn"
        >
          <el-icon><Close /></el-icon>
        </el-button>
      </div>

      <!-- 快速操作 -->
      <div class="panel-quick-actions">
        <el-button
          type="primary"
          @click="handleCreateActivity"
          class="create-activity-btn"
        >
          <el-icon><Plus /></el-icon>
          创建活动
        </el-button>
      </div>

      <!-- 筛选器 -->
      <div class="panel-filters">
        <el-radio-group
          v-model="activeFilter"
          @change="handleFilterChange"
          size="small"
          class="filter-group"
        >
          <el-radio-button value="joined">已参与</el-radio-button>
          <el-radio-button value="created">我组织的</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 内容区域 -->
      <div class="panel-content">
        <div v-if="loading" class="loading-state">
          <div v-for="i in 3" :key="i" class="activity-skeleton">
            <el-skeleton animated>
              <template #template>
                <div class="skeleton-header">
                  <el-skeleton-item variant="image" style="width: 80px; height: 80px;" />
                  <div class="skeleton-info">
                    <el-skeleton-item variant="h3" style="width: 60%;" />
                    <el-skeleton-item variant="text" style="width: 80%;" />
                    <el-skeleton-item variant="text" style="width: 40%;" />
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>
        </div>

        <div v-else-if="filteredActivities.length === 0" class="empty-state">
          <el-icon class="empty-icon"><Calendar /></el-icon>
          <p class="empty-text">{{ getEmptyText() }}</p>
          <el-button
            type="primary"
            size="small"
            @click="handleCreateActivity"
            class="create-btn"
          >
            创建活动
          </el-button>
        </div>

        <div v-else class="activities-list">
          <div
            v-for="activity in filteredActivities"
            :key="activity.id"
            class="activity-item"
            @click="handleActivityClick(activity.id)"
          >
            <!-- 活动图片 -->
            <div class="activity-image-container">
              <img
                v-if="activity.image"
                :src="activity.image"
                :alt="activity.title"
                class="activity-image"
              />
              <div v-else class="image-placeholder">
                <el-icon><Picture /></el-icon>
              </div>
            </div>

            <!-- 活动信息 -->
            <div class="activity-info">
              <div class="activity-header">
                <h4 class="activity-title">{{ activity.title }}</h4>
                <el-tag
                  :type="getStatusType(activity.status)"
                  size="small"
                  class="status-tag"
                >
                  {{ getStatusText(activity.status) }}
                </el-tag>
              </div>

              <div class="activity-meta">
                <div class="meta-item">
                  <el-icon class="meta-icon"><Clock /></el-icon>
                  <span>{{ formatDateTimeValue(activity.startTime) }}</span>
                </div>
                <div class="meta-item">
                  <el-icon class="meta-icon"><Location /></el-icon>
                  <span>{{ activity.venue }}</span>
                </div>
                <div class="meta-item">
                  <el-icon class="meta-icon"><User /></el-icon>
                  <span>{{ activity.currentParticipants }}/{{ activity.maxParticipants }}人</span>
                </div>
              </div>

              <div class="activity-footer">
                <div class="activity-type">
                  <el-tag type="info" size="small">
                    {{ getActivityTypeText(activity.type) }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作 -->
      <div class="panel-footer">
        <el-button
          type="primary"
          @click="viewAllActivities"
          class="view-all-btn"
        >
          查看全部活动记录
        </el-button>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, Clock, Close, Location, Picture, Plus, User } from '@element-plus/icons-vue'
import { useActivity } from '@/composables/useActivity'
import { useAuthStore } from '@/stores/auth.ts'
import { formatDateTimeValue, getActivityTypeText, getStatusText, getStatusType } from '@/utils/format'

interface Props {
  visible: boolean
}

interface Emits {
  (e: 'close'): void
  (e: 'update:visible', value: boolean): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const router = useRouter()
const activeFilter = ref<'joined' | 'created'>('joined')
const loading = ref(false)
const authStore = useAuthStore()
const currentUser = computed(() => authStore.currentUser)
const filteredActivities = ref([] as any[])

const {
  fetchUserActivities
} = useActivity()

const getEmptyText = () => {
  switch (activeFilter.value) {
    case 'joined':
      return '您还没有参与任何活动'
    case 'created':
      return '您还没有创建任何活动'
    default:
      return '暂无活动记录'
  }
}

const handleFilterChange = async () => {
  await fetchActivities(activeFilter.value)
}

const fetchActivities = async (filterType: string = 'joined') => {
  try {
    loading.value = true;
    let data;
    if (filterType === 'joined') {
      data = (await fetchUserActivities('JOINED'));
    } else if (filterType === 'created') {
      data = (await fetchUserActivities('CREATED'));
    }
    if (data) {
      filteredActivities.value = data.content
        .filter(activity => activity.status !== 'COMPLETED' && activity.status !== 'CANCELLED')
        .sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime())
    }
  } catch (error) {
    console.error('获取我的活动失败:', error);
  } finally {
    loading.value = false;
  }
};

const handleActivityClick = (activityId: number) => {
  emit('close')
  router.push(`/activities/${activityId}`)
}

const handleCreateActivity = () => {
  emit('close')
  router.push('/activities/create')
}

const viewAllActivities = () => {
  emit('close')
  router.push(`/users/${currentUser.value.id}?tab=activities`)
}

watch(() => props.visible, async (newVisible) => {
  if (newVisible) {
    await fetchActivities(activeFilter.value)
  }
})
</script>

<style scoped>
.panel-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1998;
  backdrop-filter: blur(4px);
}

.overlay-enter-active,
.overlay-leave-active {
  transition: opacity 0.3s ease;
}

.overlay-enter-from,
.overlay-leave-to {
  opacity: 0;
}

.my-activities-panel {
  position: fixed;
  top: 0;
  left: 0;
  width: 420px;
  height: 100vh;
  background: var(--bg-color-primary);
  box-shadow: var(--shadow-lg);
  z-index: 1999;
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--border-color-light);
}

.slide-left-enter-active,
.slide-left-leave-active {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-left-enter-from,
.slide-left-leave-to {
  transform: translateX(-100%);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--border-color-light);
  background: var(--bg-color-primary);
}

.panel-title {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
}

.close-btn {
  color: var(--text-color-secondary);
  padding: var(--spacing-xs);
}

.close-btn:hover {
  color: var(--text-color-primary);
  background: var(--bg-color-secondary);
}

.panel-quick-actions {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--border-color-light);
}

.create-activity-btn {
  width: 100%;
  border-radius: var(--border-radius-lg);
  font-weight: var(--font-weight-medium);
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
}

.panel-filters {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--border-color-light);
  background: var(--bg-color-secondary);
}

.filter-group {
  width: 100%;
}

.filter-group :deep(.el-radio-button) {
  flex: 1;
}

.filter-group :deep(.el-radio-button__inner) {
  width: 100%;
  border-radius: var(--border-radius-md);
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg);
}

.loading-state {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.activity-skeleton {
  padding: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-color-light);
}

.skeleton-header {
  display: flex;
  gap: var(--spacing-md);
}

.skeleton-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.empty-state {
  text-align: center;
  padding: var(--spacing-3xl) var(--spacing-lg);
}

.empty-icon {
  font-size: var(--font-size-4xl);
  color: var(--text-color-tertiary);
  margin-bottom: var(--spacing-lg);
}

.empty-text {
  margin: 0 0 var(--spacing-xl) 0;
  color: var(--text-color-secondary);
  font-size: var(--font-size-base);
}

.create-btn {
  border-radius: var(--border-radius-lg);
}

.activities-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.activity-item {
  background: var(--bg-color-primary);
  border: 1px solid var(--border-color-light);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-md);
  cursor: pointer;
  transition: var(--transition-base);
  display: flex;
  gap: var(--spacing-md);
}

.activity-item:hover {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.activity-image-container {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

.activity-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: var(--bg-color-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-tertiary);
  font-size: var(--font-size-xl);
}

.activity-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  min-width: 0;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-sm);
}

.activity-title {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  flex: 1;
}

.status-tag {
  flex-shrink: 0;
}

.activity-meta {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--text-color-secondary);
}

.meta-icon {
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
  flex-shrink: 0;
}

.activity-footer {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-top: var(--spacing-xs);
}

.activity-type {
  flex-shrink: 0;
}

.panel-footer {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-top: 1px solid var(--border-color-light);
  background: var(--bg-color-secondary);
}

.view-all-btn {
  width: 100%;
  border-radius: var(--border-radius-lg);
  font-weight: var(--font-weight-medium);
}

/* 滚动条样式 */
.panel-content::-webkit-scrollbar {
  width: 4px;
}

.panel-content::-webkit-scrollbar-track {
  background: var(--bg-color-secondary);
}

.panel-content::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 2px;
}

.panel-content::-webkit-scrollbar-thumb:hover {
  background: var(--text-color-tertiary);
}

@media (max-width: 480px) {
  .my-activities-panel {
    width: 100vw;
  }

  .panel-header,
  .panel-quick-actions,
  .panel-filters,
  .panel-footer {
    padding-left: var(--spacing-lg);
    padding-right: var(--spacing-lg);
  }
}
</style>
