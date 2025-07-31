// src/components/layout/ActivitySidebar.vue
<template>
  <aside class="activity-sidebar">
    <div class="sidebar-section">
      <h4>活动信息</h4>
      <div class="placeholder-content">
        <p>活动详情加载中...</p>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
// 暂时占位，后续实现具体功能
</script>

<style scoped>
.activity-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-section {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  padding: 16px;
}

.sidebar-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.placeholder-content {
  color: var(--el-text-color-regular);
}
</style>
