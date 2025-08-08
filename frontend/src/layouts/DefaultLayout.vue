<template>
  <div class="default-layout">
    <AppHeader />
    <main class="main-content">
      <div class="content-area">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/common/AppHeader.vue'
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
  /* 移除 padding-top，让 header 直接覆盖在内容上 */
}

.content-area {
  flex: 1;
  width: 100%;
  background: var(--bg-color-primary);
}
</style>
