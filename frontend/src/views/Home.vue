<template>
  <div class="home-page">
    <!-- 全屏封面英雄区域 -->
    <section class="hero-section" ref="heroSection">
      <div class="hero-background">
        <div class="hero-gradient"></div>
      </div>

      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            PLAYGROUND FITNESS WAS CENTRAL IN<br>
            HELPING ME GAIN THE STAMINA FOR MY<br>
            RECENT MARATHON.
          </h1>
          <p class="hero-author">-Alejandro Jimenez</p>
        </div>

        <!-- 下拉提示 -->
        <div class="scroll-indicator" @click="scrollToActivities">
          <el-icon class="scroll-arrow"><ArrowDown /></el-icon>
          <span class="scroll-text">浏览活动</span>
        </div>
      </div>
    </section>

    <!-- 活动列表区域 -->
    <section class="activities-section" ref="activitiesSection">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">发现活动</h2>
          <p class="section-subtitle">找到适合你的运动活动</p>
        </div>

        <!-- 移动端搜索 -->
        <div class="mobile-search" v-if="!showSearch">
          <ActivitySearch
            v-model="searchKeyword"
            @search="handleSearch"
            :placeholder="'搜索活动...'"
            class="mobile-search-input"
          />

          <el-button
            @click="showMobileFilters = !showMobileFilters"
            class="mobile-filter-btn"
          >
            筛选
            <el-icon>
              <component :is="showMobileFilters ? 'ArrowUp' : 'ArrowDown'" />
            </el-icon>
          </el-button>
        </div>

        <!-- 移动端筛选器 -->
        <Transition name="filter-slide">
          <div v-show="showMobileFilters && !showSearch" class="mobile-filter-container">
            <ActivityFilter
              v-model="filterParams"
              @filter="handleFilter"
            />
          </div>
        </Transition>

        <!-- 活动列表 -->
        <div class="activities-container">
          <div v-if="loading && activities.length === 0" class="loading-state">
            <div class="loading-grid">
              <el-skeleton
                v-for="i in 6"
                :key="i"
                animated
                class="activity-skeleton"
              >
                <template #template>
                  <el-skeleton-item variant="image" style="width: 100%; height: 200px;" />
                  <div style="padding: 14px;">
                    <el-skeleton-item variant="h3" style="width: 80%;" />
                    <el-skeleton-item variant="text" style="width: 60%; margin-top: 12px;" />
                    <el-skeleton-item variant="text" style="width: 40%; margin-top: 8px;" />
                  </div>
                </template>
              </el-skeleton>
            </div>
          </div>

          <div v-else-if="activities.length === 0" class="empty-state">
            <el-icon class="empty-icon"><Calendar /></el-icon>
            <h3 class="empty-title">暂无活动</h3>
            <p class="empty-text">还没有找到合适的活动</p>
            <el-button
              v-if="isLoggedIn"
              type="primary"
              @click="$router.push('/activities/create')"
              class="create-activity-btn"
            >
              创建第一个活动
            </el-button>
          </div>

          <div v-else class="activities-grid">
            <ActivityCard
              v-for="activity in activities"
              :key="activity.id"
              :activity="activity"
              @register="handleRegister"
              @view-detail="handleViewDetail"
              class="activity-card-item"
            />
          </div>

          <!-- 加载更多 -->
          <div v-if="hasMore && activities.length > 0" class="load-more-section">
            <el-button
              @click="loadMore"
              :loading="loading"
              size="large"
              class="load-more-btn"
            >
              加载更多活动
            </el-button>
          </div>

          <!-- 加载中状态 -->
          <div v-if="loading && activities.length > 0" class="loading-more">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载更多活动中...</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowDown, Calendar, Loading } from '@element-plus/icons-vue'
import { useAuth } from '@/composables/useAuth'
import { useActivity } from '@/composables/useActivity'
import ActivityCard from '@/components/activity/ActivityCard.vue'
import ActivitySearch from '@/components/activity/ActivitySearch.vue'
import ActivityFilter from '@/components/activity/ActivityFilter.vue'
import type { ActivitySearchParams } from '@/types/activity'

const route = useRoute()
const { isLoggedIn } = useAuth()
const {
  activities,
  loading,
  pagination,
  searchActivities,
  registerActivity
} = useActivity()

const heroSection = ref<HTMLElement>()
const activitiesSection = ref<HTMLElement>()
const searchKeyword = ref('')
const filterParams = ref({})
const showMobileFilters = ref(false)

const showSearch = computed(() => route.path === '/' && window.innerWidth > 768)

const hasMore = computed(() => {
  return pagination.value.page < pagination.value.totalPages - 1
})

const handleRegister = async (activityId: number) => {
  await registerActivity(activityId)
  await searchActivities({
    page: 0,
    size: pagination.value.size
  })
}

const handleViewDetail = (activityId: number) => {
  window.open(`/activities/${activityId}`, '_blank')
}

const handleSearch = async (keyword: string) => {
  searchKeyword.value = keyword
  await searchActivities({
    keyword,
    ...filterParams.value,
    page: 0,
    size: 12
  })
}

const handleFilter = async (params: any) => {
  filterParams.value = params
  await searchActivities({
    keyword: searchKeyword.value,
    ...params,
    page: 0,
    size: 12
  })
}

const loadMore = async () => {
  await searchActivities({
    keyword: searchKeyword.value,
    ...filterParams.value,
    page: pagination.value.page + 1,
    size: pagination.value.size
  })
}

const scrollToActivities = () => {
  nextTick(() => {
    activitiesSection.value?.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    })
  })
}

// 监听头部搜索事件
const handleHeaderSearch = (event: CustomEvent) => {
  const { keyword, filters } = event.detail
  searchKeyword.value = keyword
  filterParams.value = filters
  searchActivities({
    keyword,
    ...filters,
    page: 0,
    size: 12
  })

  scrollToActivities()
}

onMounted(() => {
  // 初始加载活动列表
  searchActivities({
    page: 0,
    size: 12,
    sortBy: 'startTime',
    sortDir: 'asc'
  })

  // 监听头部搜索事件
  window.addEventListener('header-search', handleHeaderSearch as EventListener)
})

onUnmounted(() => {
  window.removeEventListener('header-search', handleHeaderSearch as EventListener)
})
</script>

<style scoped>
.home-page {
  width: 100%;
  min-height: 100vh;
}

/* 英雄区域样式 */
.hero-section {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin-left: calc(-50vw + 50%);
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.hero-gradient {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  /* 后续这里会替换为背景图片 */
  /* background-image: url('背景图片URL'); */
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.hero-content {
  position: relative;
  z-index: 10;
  text-align: center;
  color: var(--text-color-inverse);
  padding: var(--spacing-xl);
  max-width: 1000px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.hero-text {
  margin-bottom: var(--spacing-3xl);
}

.hero-title {
  font-size: clamp(2rem, 4vw, 3.5rem);
  font-weight: var(--font-weight-extrabold);
  line-height: 1.2;
  margin: 0 0 var(--spacing-xl) 0;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.hero-author {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-medium);
  margin: 0;
  opacity: 0.9;
}

.scroll-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  transition: var(--transition-base);
  position: absolute;
  bottom: var(--spacing-2xl);
}

.scroll-indicator:hover {
  transform: translateY(-5px);
}

.scroll-arrow {
  font-size: var(--font-size-2xl);
  animation: bounce 2s infinite;
}

.scroll-text {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  opacity: 0.8;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

/* 活动区域样式 */
.activities-section {
  background: var(--bg-color-primary);
  padding: var(--spacing-3xl) 0;
  min-height: 100vh;
}

.section-container {
  max-width: var(--container-max-width);
  margin: 0 auto;
  padding: 0 var(--spacing-xl);
  width: 100%;
}

.section-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
}

.section-title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-extrabold);
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-color-primary);
}

.section-subtitle {
  font-size: var(--font-size-lg);
  color: var(--text-color-secondary);
  margin: 0;
}

.mobile-search {
  display: none;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  align-items: center;
}

.mobile-search-input {
  flex: 1;
}

.mobile-filter-btn {
  flex-shrink: 0;
}

.mobile-filter-container {
  margin-bottom: var(--spacing-lg);
}

.filter-slide-enter-active,
.filter-slide-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.filter-slide-enter-from,
.filter-slide-leave-to {
  max-height: 0;
  opacity: 0;
}

.filter-slide-enter-to,
.filter-slide-leave-from {
  max-height: 400px;
  opacity: 1;
}

.activities-container {
  margin-top: var(--spacing-xl);
}

.loading-state {
  padding: var(--spacing-xl) 0;
}

.loading-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-xl);
}

.activity-skeleton {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.empty-state {
  text-align: center;
  padding: var(--spacing-3xl) var(--spacing-lg);
}

.empty-icon {
  font-size: var(--font-size-5xl);
  color: var(--text-color-tertiary);
  margin-bottom: var(--spacing-lg);
}

.empty-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-color-secondary);
}

.empty-text {
  font-size: var(--font-size-lg);
  color: var(--text-color-tertiary);
  margin: 0 0 var(--spacing-xl) 0;
}

.create-activity-btn {
  font-size: var(--font-size-base);
  padding: var(--spacing-md) var(--spacing-xl);
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
}

.activity-card-item {
  transition: var(--transition-base);
}

.activity-card-item:hover {
  transform: translateY(-5px);
}

.load-more-section {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}

.load-more-btn {
  font-size: var(--font-size-base);
  padding: var(--spacing-md) var(--spacing-2xl);
  border-radius: var(--border-radius-full);
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xl);
  color: var(--text-color-secondary);
  font-size: var(--font-size-base);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .section-container {
    padding: 0 var(--spacing-lg);
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: clamp(1.5rem, 6vw, 2.5rem);
    padding: 0 var(--spacing-md);
    line-height: 1.3;
  }

  .hero-author {
    font-size: var(--font-size-base);
  }

  .mobile-search {
    display: flex;
  }

  .activities-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }

  .section-container {
    padding: 0 var(--spacing-md);
  }

  .section-title {
    font-size: var(--font-size-3xl);
  }

  .scroll-indicator {
    bottom: var(--spacing-xl);
  }
}

@media (max-width: 480px) {
  .hero-content {
    padding: var(--spacing-lg);
  }

  .section-header {
    margin-bottom: var(--spacing-xl);
  }

  .section-title {
    font-size: var(--font-size-2xl);
  }

  .loading-grid,
  .activities-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-md);
  }
}
</style>
