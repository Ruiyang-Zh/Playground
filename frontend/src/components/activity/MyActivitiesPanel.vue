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

      <div class="panel-filters">
        <el-radio-group
          v-model="activeFilter"
          @change="handleFilterChange"
          size="small"
        >
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="joined">已参与</el-radio-button>
          <el-radio-button label="created">我组织的</el-radio-button>
        </el-radio-group>
      </div>

      <div class="panel-content">
        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="4" animated />
        </div>

        <div v-else-if="activities.length === 0" class="empty-state">
          <el-icon class="empty-icon"><Calendar /></el-icon>
          <p class="empty-text">暂无活动记录</p>
        </div>

        <div v-else class="activities-list">
          <div
            v-for="activity in activities"
            :key="activity.id"
            class="activity-item"
            @click="handleActivityClick(activity.id)"
          >
            <div class="activity-image">
              <img
                v-if="activity.image"
                :src="activity.image"
                :alt="activity.title"
              />
              <div v-else class="image-placeholder">
                <el-icon><Picture /></el-icon>
              </div>
              <el-tag
                :type="getStatusType(activity.status)"
                size="small"
                class="status-tag"
              >
                {{ getStatusText(activity.status) }}
              </el-tag>
            </div>

            <div class="activity-info">
              <h4 class="activity-title">{{ activity.title }}</h4>
              <div class="activity-meta">
                <div class="meta-item">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatDateTime(activity.startTime) }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ activity.venue }}</span>
                </div>
              </div>
              <div class="activity-type">
                <el-tag size="small" type="info">
                  {{ getActivityTypeText(activity.type) }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

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
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Close,
  Calendar,
  Picture,
  Clock,
  Location
} from '@element-plus/icons-vue'
import { useActivity } from '@/composables/useActivity'
import {
  formatDateTime,
  getStatusText,
  getStatusType,
  getActivityTypeText
} from '@/utils/format'
import type { ActivityBriefResponse } from '@/types/activity'

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
const activeFilter = ref('all')
const activities = ref<ActivityBriefResponse[]>([])
const loading = ref(false)

const handleFilterChange = async (value: string) => {
  await fetchMyActivities(value)
}

const fetchMyActivities = async (filterType: string = 'all') => {
  try {
    loading.value = true
    // 这里调用 API 获取我的活动数据
    // 根据 filterType 筛选不同类型的活动
    const params = {
      page: 0,
      size: 20,
      participationType: filterType === 'joined' ? 'JOINED' :
        filterType === 'created' ? 'CREATED' : undefined
    }

    // 临时模拟数据
    setTimeout(() => {
      activities.value = []
      loading.value = false
    }, 500)
  } catch (error) {
    console.error('获取我的活动失败:', error)
    loading.value = false
  }
}

const handleActivityClick = (activityId: number) => {
  emit('close')
  router.push(`/activities/${activityId}`)
}

const viewAllActivities = () => {
  emit('close')
  router.push('/profile?tab=activities')
}

watch(() => props.visible, (newVisible) => {
  if (newVisible) {
    fetchMyActivities(activeFilter.value)
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
}

.overlay-enter-active,
.overlay-leave-active {
  transition: opacity var(--transition-base);
}

.overlay-enter-from,
.overlay-leave-to {
  opacity: 0;
}

.my-activities-panel {
  position: fixed;
  top: 0;
  left: 0;
  width: 400px;
  height: 100vh;
  background: var(--bg-color-primary);
  box-shadow: var(--shadow-lg);
  z-index: 1999;
  display: flex;
  flex-direction: column;
}

.slide-left-enter-active,
.slide-left-leave-active {
  transition: transform var(--transition-base);
}

.slide-left-enter-from,
.slide-left-leave-to {
  transform: translateX(-100%);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
}

.panel-title {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.close-btn {
  color: var(--text-color-secondary);
}

.panel-filters {
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-md);
}

.loading-state,
.empty-state {
  padding: var(--spacing-xl);
  text-align: center;
}

.empty-icon {
  font-size: var(--font-size-4xl);
  color: var(--text-color-tertiary);
  margin-bottom: var(--spacing-md);
}

.empty-text {
  margin: 0;
  color: var(--text-color-secondary);
}

.activities-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.activity-item {
  background: var(--bg-color-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-md);
  cursor: pointer;
  transition: var(--transition-base);
}

.activity-item:hover {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
}

.activity-image {
  position: relative;
  margin-bottom: var(--spacing-sm);
}

.activity-image img {
  width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: var(--border-radius-md);
}

.image-placeholder {
  width: 100%;
  height: 100px;
  background: var(--bg-color-secondary);
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-tertiary);
  font-size: var(--font-size-xl);
}

.status-tag {
  position: absolute;
  top: var(--spacing-sm);
  right: var(--spacing-sm);
}

.activity-info {
  display: flex;
  flex-direction: column;
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

.meta-item .el-icon {
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
}

.activity-type {
  align-self: flex-start;
}

.panel-footer {
  padding: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.view-all-btn {
  width: 100%;
}

@media (max-width: 480px) {
  .my-activities-panel {
    width: 100vw;
  }
}
</style>
