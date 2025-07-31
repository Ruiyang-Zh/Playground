<template>
  <aside class="main-sidebar">
    <div class="sidebar-header">
      <h3 class="sidebar-title">近期活动</h3>
      <el-button
        text
        size="small"
        @click="refreshRecentActivities"
        :loading="loading"
      >
        <el-icon><Refresh /></el-icon>
      </el-button>
    </div>

    <div class="sidebar-content">
      <div v-if="loading && recentActivities.length === 0" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>

      <div
        v-else-if="recentActivities.length === 0"
        class="empty-state"
      >
        <el-icon class="empty-icon"><Calendar /></el-icon>
        <p class="empty-text">暂无近期活动</p>
        <p class="empty-hint">参与活动后会在这里显示</p>
      </div>

      <div v-else class="activity-list">
        <div
          v-for="activity in recentActivities"
          :key="activity.id"
          class="recent-activity-item"
          @click="$router.push(`/activities/${activity.id}`)"
        >
          <div class="activity-image-container">
            <img
              v-if="activity.image"
              :src="activity.image"
              :alt="activity.title"
              class="activity-image"
            />
            <div v-else class="activity-image-placeholder">
              <el-icon><Picture /></el-icon>
            </div>
            <div class="activity-status-badge">
              <el-tag
                :type="getStatusType(activity.status)"
                size="small"
              >
                {{ getStatusText(activity.status) }}
              </el-tag>
            </div>
          </div>

          <div class="activity-info">
            <h4 class="activity-title">{{ activity.title }}</h4>
            <div class="activity-meta">
              <div class="meta-item">
                <el-icon class="meta-icon"><Clock /></el-icon>
                <span>{{ formatDateTime(activity.startTime) }}</span>
              </div>
              <div class="meta-item">
                <el-icon class="meta-icon"><Location /></el-icon>
                <span>{{ activity.venue }}</span>
              </div>
              <div class="meta-item">
                <el-icon class="meta-icon"><User /></el-icon>
                <span>{{ activity.currentParticipants }}/{{ activity.maxParticipants }}</span>
              </div>
            </div>
            <div class="activity-type">
              <el-tag type="info" size="small">
                {{ getActivityTypeText(activity.type) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import {
  Refresh,
  Calendar,
  Picture,
  Clock,
  Location,
  User
} from '@element-plus/icons-vue'
import { useActivityStore } from '@/stores/activity'
import { useAuth } from '@/composables/useAuth'
import { formatDateTime, getStatusText, getStatusType, getActivityTypeText } from '@/utils/format'
import { storeToRefs } from 'pinia'

const activityStore = useActivityStore()
const { isLoggedIn } = useAuth()
const { recentActivities, loading } = storeToRefs(activityStore)

const refreshRecentActivities = async () => {
  if (isLoggedIn.value) {
    await activityStore.fetchRecentActivities()
  }
}

onMounted(() => {
  refreshRecentActivities()
})
</script>

<style scoped>
.main-sidebar {
  width: var(--sidebar-width);
  height: calc(100vh - var(--header-height));
  background: var(--bg-color-primary);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  position: sticky;
  top: var(--header-height);
}

.sidebar-header {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.sidebar-title {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-md);
}

.loading-state,
.empty-state {
  padding: var(--spacing-xl) var(--spacing-lg);
  text-align: center;
}

.empty-icon {
  font-size: var(--font-size-4xl);
  color: var(--text-color-tertiary);
  margin-bottom: var(--spacing-md);
}

.empty-text {
  margin: 0 0 var(--spacing-sm) 0;
  color: var(--text-color-secondary);
  font-weight: var(--font-weight-medium);
}

.empty-hint {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.recent-activity-item {
  background: var(--bg-color-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-md);
  cursor: pointer;
  transition: var(--transition-base);
}

.recent-activity-item:hover {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
}

.activity-image-container {
  position: relative;
  margin-bottom: var(--spacing-sm);
}

.activity-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: var(--border-radius-md);
}

.activity-image-placeholder {
  width: 100%;
  height: 120px;
  background: var(--bg-color-secondary);
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-tertiary);
  font-size: var(--font-size-2xl);
}

.activity-status-badge {
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
  line-height: 1.4;
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
}

.activity-type {
  align-self: flex-start;
}

@media (max-width: 768px) {
  .main-sidebar {
    display: none;
  }
}
</style>
