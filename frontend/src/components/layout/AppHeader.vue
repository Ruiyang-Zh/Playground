<template>
  <header class="app-header" :class="headerClass">
    <div class="header-container">
      <!-- Logo -->
      <div class="header-brand">
        <router-link to="/" class="brand-link">
          <span class="brand-text">PLAYGROUND</span>
        </router-link>
      </div>

      <!-- 中间搜索区域 -->
      <div class="header-center" v-if="showSearch">
        <div class="search-container">
          <ActivitySearch
            v-model="searchKeyword"
            @search="handleSearch"
            :placeholder="''"
            :theme="currentTheme"
            class="header-search"
          />

          <!-- 筛选按钮 -->
          <button
            @click="toggleFilter"
            class="filter-toggle-btn"
            type="button"
          >
            <el-icon class="filter-menu-icon"><Operation /></el-icon>
          </button>
        </div>

        <!-- 筛选器下拉面板 -->
        <Transition name="filter-dropdown">
          <div v-show="showFilters" class="filter-dropdown">
            <ActivityFilter
              v-model="filterParams"
              @filter="handleFilter"
              compact
            />
          </div>
        </Transition>
      </div>

      <!-- 右侧操作区 -->
      <div class="header-actions">
        <template v-if="isLoggedIn">
          <!-- 我的活动按钮 -->
          <button
            class="header-action-btn my-activities-btn"
            @click="toggleMyActivities"
            type="button"
          >
            我的活动
          </button>

          <!-- 用户菜单 -->
          <el-dropdown trigger="click" @command="handleUserAction">
            <div class="user-menu-trigger">
              <UserAvatar
                :user="currentUser"
                :clickable="false"
                :show-hover="false"
                size="small"
              />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="create">创建活动</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <button
            class="header-action-btn join-btn"
            @click="$router.push('/auth/register')"
            type="button"
          >
            JOIN US
          </button>
        </template>
      </div>
    </div>

    <!-- 我的活动侧边面板 -->
    <MyActivitiesPanel
      v-model:visible="showMyActivitiesPanel"
      @close="showMyActivitiesPanel = false"
    />
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch} from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Operation } from '@element-plus/icons-vue'
import { useAuth } from '@/composables/useAuth'
import UserAvatar from '@/components/common/UserAvatar.vue'
import ActivitySearch from '@/components/activity/ActivitySearch.vue'
import ActivityFilter from '@/components/activity/ActivityFilter.vue'
import MyActivitiesPanel from '@/components/activity/MyActivitiesPanel.vue'

const router = useRouter()
const route = useRoute()
const { isLoggedIn, currentUser, logout } = useAuth()

// 滚动位置状态
const scrollY = ref(0)
const showSearch = computed(() => route.path === '/')
const showFilters = ref(false)
const showMyActivitiesPanel = ref(false)

// 搜索相关
const searchKeyword = ref('')
const filterParams = ref({})

// 计算当前主题 - 简化逻辑
const currentTheme = computed(() => {
  const isHomePage = route.path === '/'
  if (!isHomePage) {
    return 'dark'
  }
  // 滚动超过50px就切换为黑色主题
  return scrollY.value > 50 ? 'dark' : 'transparent'
})

// 计算头部样式类
const headerClass = computed(() => {
  const isHomePage = route.path === '/'
  if (!isHomePage) {
    return 'theme-dark'
  }
  // 滚动超过50px就切换为黑色主题
  const isDark = scrollY.value > 50
  console.log('ScrollY:', scrollY.value, 'Theme:', isDark ? 'dark' : 'transparent')
  return isDark ? 'theme-dark' : 'theme-transparent'
})

// 直接的滚动处理函数
const updateScrollY = () => {
  const newScrollY = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0
  scrollY.value = newScrollY
  console.log('Scroll updated:', newScrollY)
}

// 创建滚动监听器
const createScrollListener = () => {
  console.log('Creating scroll listener...')

  // 移除可能存在的旧监听器
  window.removeEventListener('scroll', updateScrollY)
  document.removeEventListener('scroll', updateScrollY)

  // 添加新的监听器
  window.addEventListener('scroll', updateScrollY, { passive: true })
  document.addEventListener('scroll', updateScrollY, { passive: true })

  // 立即检查滚动位置
  updateScrollY()

  console.log('Scroll listener created, initial scroll:', scrollY.value)
}

// 移除滚动监听器
const removeScrollListener = () => {
  console.log('Removing scroll listener...')
  window.removeEventListener('scroll', updateScrollY)
  document.removeEventListener('scroll', updateScrollY)
}

// 测试滚动功能
const testScroll = () => {
  console.log('Testing scroll detection...')

  // 手动设置一些测试值
  scrollY.value = 100
  setTimeout(() => {
    scrollY.value = 0
  }, 1000)

  // 检查滚动元素
  console.log('Window scrollY:', window.scrollY)
  console.log('Document scrollTop:', document.documentElement.scrollTop)
  console.log('Body scrollTop:', document.body.scrollTop)
  console.log('Page height:', document.body.scrollHeight)
  console.log('Viewport height:', window.innerHeight)
}

const toggleFilter = () => {
  showFilters.value = !showFilters.value
}

const toggleMyActivities = () => {
  showMyActivitiesPanel.value = !showMyActivitiesPanel.value
}

const handleSearch = (keyword: string) => {
  window.dispatchEvent(new CustomEvent('header-search', {
    detail: { keyword, filters: filterParams.value }
  }))
}

const handleFilter = (params: any) => {
  filterParams.value = params
  showFilters.value = false
  window.dispatchEvent(new CustomEvent('header-search', {
    detail: { keyword: searchKeyword.value, filters: params }
  }))
}

const handleUserAction = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'create':
      router.push('/activities/create')
      break
    case 'logout':
      logout()
      break
  }
}

onMounted(() => {
  console.log('AppHeader mounted - setting up scroll listener')

  // 等待DOM完全渲染
  nextTick(() => {
    createScrollListener()

    // 延迟测试
    setTimeout(() => {
      testScroll()
    }, 500)
  })
})

onUnmounted(() => {
  console.log('AppHeader unmounted')
  removeScrollListener()
})

// 监听路由变化
watch(() => route.path, (newPath) => {
  console.log('Route changed to:', newPath)
  nextTick(() => {
    createScrollListener()
  })
})

// 调试：监听scrollY变化
watch(scrollY, (newVal, oldVal) => {
  console.log('ScrollY reactive change:', oldVal, '->', newVal)
})

// 暴露测试函数到全局（仅开发环境）
if (import.meta.env.DEV) {
  (window as any).testHeaderScroll = () => {
    console.log('Manual scroll test triggered')
    updateScrollY()
    testScroll()
  }
}
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: var(--header-height);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  width: 100%;
}

/* 透明主题 - 仅在封面全屏状态 */
.app-header.theme-transparent {
  background: transparent;
  color: var(--text-color-inverse);
  border-bottom: 1px solid transparent;
  backdrop-filter: none;
  box-shadow: none;
}

/* 黑色主题 - 滚动后或非主页 */
.app-header.theme-dark {
  background: rgba(0, 0, 0, 0.95);
  color: var(--text-color-inverse);
  backdrop-filter: blur(20px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-xl);
  position: relative;
  max-width: none;
  margin: 0;
}

.header-brand {
  flex-shrink: 0;
  z-index: 10;
}

.brand-link {
  text-decoration: none;
  color: inherit;
  transition: color 0.3s ease;
}

.brand-text {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-extrabold);
  letter-spacing: 1px;
}

.header-center {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 450px;
  max-width: 40%;
}

.search-container {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.header-search {
  flex: 1;
}

.filter-toggle-btn {
  color: inherit;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: var(--spacing-sm);
  min-width: auto;
  width: 40px;
  height: 40px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  cursor: pointer;
  outline: none;
}

.filter-toggle-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
}

.filter-menu-icon {
  font-size: var(--font-size-lg);
}

.filter-dropdown {
  position: absolute;
  top: calc(100% + var(--spacing-md));
  left: 0;
  right: 0;
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  padding: var(--spacing-lg);
  z-index: 1001;
  border: 1px solid var(--border-color-light);
}

.filter-dropdown-enter-active,
.filter-dropdown-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: top center;
}

.filter-dropdown-enter-from,
.filter-dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px) scaleY(0.95);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  z-index: 10;
}

/* 统一的头部按钮样式 */
.header-action-btn {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: inherit;
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-base);
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
  font-family: inherit;
}

.header-action-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-1px);
}

.header-action-btn:active {
  transform: translateY(0);
}

/* JOIN US 按钮特殊样式 */
.join-btn {
  border-radius: var(--border-radius-full);
  padding: var(--spacing-sm) var(--spacing-xl);
  font-weight: var(--font-weight-semibold);
}

.join-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.user-menu-trigger {
  cursor: pointer;
  padding: var(--spacing-xs);
  border-radius: var(--border-radius-md);
  transition: all 0.3s ease;
}

.user-menu-trigger:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* 黑色主题下的按钮样式增强 */
.app-header.theme-dark .header-action-btn {
  border-color: rgba(255, 255, 255, 0.4);
}

.app-header.theme-dark .header-action-btn:hover {
  border-color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.12);
}

.app-header.theme-dark .filter-toggle-btn {
  border-color: rgba(255, 255, 255, 0.4);
}

.app-header.theme-dark .filter-toggle-btn:hover {
  border-color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.12);
}

@media (max-width: 1024px) {
  .header-center {
    width: 350px;
    max-width: 35%;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 var(--spacing-lg);
  }

  .header-center {
    display: none;
  }

  .brand-text {
    font-size: var(--font-size-lg);
  }

  .header-action-btn {
    padding: var(--spacing-sm) var(--spacing-md);
    font-size: var(--font-size-sm);
  }
}

@media (max-width: 480px) {
  .header-container {
    padding: 0 var(--spacing-md);
  }

  .header-actions {
    gap: var(--spacing-md);
  }

  .join-btn {
    padding: var(--spacing-sm) var(--spacing-lg);
  }
}
</style>
