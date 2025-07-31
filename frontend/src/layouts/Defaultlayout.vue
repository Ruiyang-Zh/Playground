<template>
  <div class="default-layout">
    <AppHeader />
    <main class="main-content">
      <div class="layout-container">
        <MainSidebar v-if="showSidebar" />
        <div class="content-area" :class="{ 'full-width': !showSidebar }">
          <router-view />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from '@/components/layout/AppHeader.vue'
import MainSidebar from '@/components/layout/MainSidebar.vue'
import { useAuth } from '@/composables/useAuth'

const route = useRoute()
const { isLoggedIn } = useAuth()

// 只有在主页且已登录时显示侧边栏
const showSidebar = computed(() => {
  return route.path === '/' && isLoggedIn.value
})
</script>

<style scoped>
.default-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.layout-container {
  flex: 1;
  display: flex;
  max-width: var(--container-max-width);
  margin: 0 auto;
  width: 100%;
}

.content-area {
  flex: 1;
  min-width: 0;
  background: var(--bg-color-secondary);
}

.content-area.full-width {
  background: var(--bg-color-primary);
}

@media (max-width: 768px) {
  .layout-container {
    flex-direction: column;
  }

  .content-area {
    background: var(--bg-color-primary);
  }
}
</style>
