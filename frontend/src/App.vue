<template>
  <div id="app">
    <!-- 根据路由选择不同的布局 -->
    <component :is="layoutComponent">
      <router-view />
    </component>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import ActivityLayout from '@/layouts/ActivityLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'

const route = useRoute()

// 根据路由路径选择布局组件
const layoutComponent = computed(() => {
  const path = route.path

  // 认证页面使用 AuthLayout
  if (path.startsWith('/auth/')) {
    return AuthLayout
  }

  // 活动详情页使用 ActivityLayout
  if (path.startsWith('/activities/') && path !== '/activities/create') {
    return ActivityLayout
  }

  // 默认使用 DefaultLayout
  return DefaultLayout
})
</script>

<style>
/* 全局样式重置和基础样式 */
* {
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
}

body {
  margin: 0;
  padding: 0;
  font-family: var(--font-family-primary);
  font-size: var(--font-size-base);
  line-height: 1.6;
  color: var(--text-color-primary);
  background-color: var(--bg-color-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  min-height: 100vh;
}

/* Element Plus 样式覆盖 */
.el-button {
  border-radius: var(--border-radius-md);
  font-weight: var(--font-weight-medium);
  transition: var(--transition-base);
}

.el-button--primary {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.el-button--primary:hover {
  background-color: var(--primary-light);
  border-color: var(--primary-light);
}

.el-card {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  transition: var(--transition-base);
}

.el-card:hover {
  box-shadow: var(--shadow-md);
}

.el-input__wrapper {
  border-radius: var(--border-radius-md);
}

.el-select .el-input__wrapper {
  border-radius: var(--border-radius-md);
}

.el-form-item__label {
  font-weight: var(--font-weight-medium);
  color: var(--text-color-primary);
}

.el-tag {
  border-radius: var(--border-radius-sm);
}

.el-avatar {
  transition: var(--transition-base);
}

.el-dropdown-menu {
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-lg);
}

.el-popover {
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-lg);
}

.el-skeleton {
  --el-skeleton-color: var(--bg-color-secondary);
  --el-skeleton-to-color: var(--border-color-light);
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: var(--bg-color-secondary);
  border-radius: var(--border-radius-sm);
}

::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: var(--border-radius-sm);
  transition: var(--transition-base);
}

::-webkit-scrollbar-thumb:hover {
  background: var(--text-color-tertiary);
}

/* Firefox 滚动条 */
* {
  scrollbar-width: thin;
  scrollbar-color: var(--border-color) var(--bg-color-secondary);
}

/* 响应式断点 */
@media (max-width: 1200px) {
  .container {
    max-width: 960px;
  }
}

@media (max-width: 992px) {
  .container {
    max-width: 720px;
  }
}

@media (max-width: 768px) {
  .container {
    max-width: 540px;
  }

  /* 移动端字体大小调整 */
  .text-5xl { font-size: var(--font-size-3xl); }
  .text-4xl { font-size: var(--font-size-2xl); }
  .text-3xl { font-size: var(--font-size-xl); }
}

@media (max-width: 576px) {
  .container {
    max-width: 100%;
    padding: 0 var(--spacing-md);
  }
}

/* 打印样式 */
@media print {
  * {
    -webkit-print-color-adjust: exact;
    color-adjust: exact;
  }

  .no-print {
    display: none !important;
  }
}

/* 高对比度模式支持 */
@media (prefers-contrast: high) {
  :root {
    --border-color: #000000;
    --text-color-secondary: #000000;
    --text-color-tertiary: #333333;
  }
}

/* 减少动画模式支持 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* Focus 可见性增强 */
.el-button:focus-visible,
.el-input__wrapper:focus-within,
.el-select:focus-within .el-input__wrapper {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
}

/* 选择文本样式 */
::selection {
  background-color: var(--primary-color);
  color: var(--text-color-inverse);
}

::-moz-selection {
  background-color: var(--primary-color);
  color: var(--text-color-inverse);
}

/* 加载动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-base);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all var(--transition-base);
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 工具类 */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.line-clamp-2 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.line-clamp-3 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

/* 图片懒加载占位符 */
.image-placeholder {
  background: linear-gradient(90deg, var(--bg-color-secondary) 25%, var(--border-color-light) 50%, var(--bg-color-secondary) 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>
