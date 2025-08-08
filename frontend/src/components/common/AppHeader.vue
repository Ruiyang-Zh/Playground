<template>
  <header class="app-header" :class="headerClass">
    <div class="header-container">
      <!-- Logo -->
      <div class="header-brand">
        <router-link to="/" class="brand-link" @click="handleLogoClick">
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
            class="header-action-btn activities-btn"
            @click="toggleMyActivities"
            type="button"
            title="我的活动"
          >
            <el-icon class="action-icon"><Calendar /></el-icon>
          </button>

          <!-- 用户菜单 -->
          <el-dropdown trigger="click" @command="handleUserAction" placement="bottom-end">
            <button class="header-action-btn user-btn" type="button" title="个人中心">
              <UserAvatar
                :user="currentUser"
                :clickable="false"
                :show-hover="false"
                size="small"
              />
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <button
            class="header-action-btn join-btn"
            @click="$router.push('/auth/login')"
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
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Operation, Calendar, User, SwitchButton } from '@element-plus/icons-vue'
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
const showFilters = ref(false)
const showMyActivitiesPanel = ref(false)

// 搜索相关
const searchKeyword = ref('')
const filterParams = ref({})

// 计算属性
const showSearch = computed(() => route.path === '/')

// 判断是否为透明主题的页面
const isTransparentPage = computed(() => {
  // return route.path === '/' || route.path === '/profile'
  return false
})

// 计算当前主题 - 修复：降低滚动阈值，任何滚动都会切换主题
const currentTheme = computed(() => {
  // if (!isTransparentPage.value) {
  //   return 'dark'
  // }
  // // 修复：任何滚动（大于1px）都切换为dark主题
  // return scrollY.value > 1 ? 'dark' : 'transparent'
  return 'dark'
})

// 计算头部样式类
const headerClass = computed(() => {
  if (!isTransparentPage.value) {
    return 'theme-dark'
  }
  // 修复：任何滚动（大于1px）都切换为dark主题
  const isDark = scrollY.value > 1
  return isDark ? 'theme-dark' : 'theme-transparent'
})

// 修复：优化滚动处理函数，使用requestAnimationFrame防抖
let rafId: number | null = null
const updateScrollY = () => {
  if (rafId !== null) {
    cancelAnimationFrame(rafId)
  }

  rafId = requestAnimationFrame(() => {
    const newScrollY = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0

    // 只有在透明页面才需要更新滚动位置
    if (isTransparentPage.value) {
      scrollY.value = Math.round(newScrollY)
    }

    rafId = null
  })
}

// 修复：统一的滚动监听器管理
const setupScrollListener = () => {
  // 先清除现有监听器
  cleanupScrollListener()

  // 只在透明页面添加滚动监听
  if (isTransparentPage.value) {
    window.addEventListener('scroll', updateScrollY, { passive: true })
    document.addEventListener('scroll', updateScrollY, { passive: true })
    // 立即更新一次滚动位置
    updateScrollY()
  } else {
    // 非透明页面重置滚动位置
    scrollY.value = 0
  }
}

const cleanupScrollListener = () => {
  if (rafId !== null) {
    cancelAnimationFrame(rafId)
    rafId = null
  }
  window.removeEventListener('scroll', updateScrollY)
  document.removeEventListener('scroll', updateScrollY)
}

// 事件处理函数
const handleLogoClick = (event: Event) => {
  if (route.path === '/') {
    // 如果已经在首页，阻止默认跳转行为，直接滚动到顶部
    event.preventDefault()
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }
  // 如果不在首页，让 router-link 正常工作
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

const handleUserAction = async (command: string) => {
  switch (command) {
    case 'profile':
      router.push(`/users/${currentUser.value?.id}`)
      break
    case 'logout':
      await logout()
      break
  }
}

watch(() => route.path, () => {
  nextTick(() => {
    setupScrollListener()
    console.log('Route changed to:', route.path, 'isTransparentPage:', isTransparentPage.value)
  })
}, { immediate: true })

// 监听滚动位置变化（用于调试）
watch(() => scrollY.value, (newVal) => {
  console.log('Scroll Y changed:', newVal, 'Theme:', currentTheme.value)
})

// 监听透明页面状态变化
watch(() => isTransparentPage.value, (newVal) => {
  console.log('Transparent page changed:', newVal)
  nextTick(() => {
    setupScrollListener()
  })
})

onMounted(() => {
  nextTick(() => {
    setupScrollListener()
    console.log('Header mounted, current route:', route.path)
  })
})

onUnmounted(() => {
  cleanupScrollListener()
})

// 监听登录状态变化
watch(() => isLoggedIn.value, (newVal) => {
  console.log('Login status changed:', newVal)
}, { immediate: true })

// 监听用户信息变化
watch(() => currentUser.value, (newUser) => {
  console.log('Current user changed:', newUser)
}, { immediate: true })
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

/* 透明主题 */
.app-header.theme-transparent {
  background: transparent;
  color: var(--text-color-inverse);
  border-bottom: 1px solid transparent;
  backdrop-filter: none;
  box-shadow: none;
}

/* 黑色主题 */
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

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  z-index: 10;
}

/* 统一的头部按钮样式 */
.header-action-btn {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: inherit;
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-base);
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

/* 我的活动按钮 */
.activities-btn {
  width: 40px;
  height: 40px;
  padding: 0;
}

.action-icon {
  font-size: var(--font-size-lg);
}

/* 用户头像按钮 */
.user-btn {
  width: 40px;
  height: 40px;
  padding: 1px; /* 很小的内边距 */
  border-radius: var(--border-radius-full);
  overflow: hidden; /* 确保头像不会超出边框 */
}

/* 强制头像填满整个按钮 */
.user-btn :deep(.user-avatar-wrapper) {
  width: 38px !important;
  height: 38px !important;
}

.user-btn :deep(.avatar-container) {
  width: 38px !important;
  height: 38px !important;
}

/* JOIN US 按钮 */
.join-btn {
  border-radius: var(--border-radius-full);
  padding: var(--spacing-sm) var(--spacing-xl);
  font-weight: var(--font-weight-semibold);
}

.join-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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

/* 下拉菜单样式 */
:deep(.el-dropdown-menu) {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border-color-light);
  padding: var(--spacing-sm);
}

:deep(.el-dropdown-menu__item) {
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

:deep(.el-dropdown-menu__item:hover) {
  background: var(--bg-color-secondary);
}

:deep(.el-dropdown-menu__item.is-divided) {
  border-top: 1px solid var(--border-color-light);
  margin-top: var(--spacing-xs);
  padding-top: var(--spacing-sm);
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

  .header-actions {
    gap: var(--spacing-sm);
  }

  .activities-btn,
  .user-btn {
    width: 36px;
    height: 36px;
  }

  .action-icon {
    font-size: var(--font-size-base);
  }
}

@media (max-width: 480px) {
  .header-container {
    padding: 0 var(--spacing-md);
  }

  .join-btn {
    padding: var(--spacing-sm) var(--spacing-lg);
    font-size: var(--font-size-sm);
  }
}
</style>
