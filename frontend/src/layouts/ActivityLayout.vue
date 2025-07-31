<template>
  <div class="activity-layout">
    <AppHeader />
    <div class="activity-detail-container">
      <main class="activity-main">
        <router-view />
      </main>
      <ActivitySidebar class="activity-sidebar" />
    </div>
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
import ActivitySidebar from '@/components/layout/ActivitySidebar.vue'
</script>
