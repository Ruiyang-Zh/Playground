<template>
  <div class="activity-search" :class="`theme-${theme}`">
    <el-input
      v-model="searchValue"
      :placeholder="placeholder"
      @keyup.enter="handleSearch"
      @input="handleInput"
      clearable
      class="search-input"
    >
      <template #prefix>
        <el-icon class="search-icon"><Search /></el-icon>
      </template>
      <template #suffix v-if="searchValue">
        <button
          @click="handleSearch"
          class="search-enter-btn"
          type="button"
        >
          <el-icon><Top /></el-icon>
        </button>
      </template>
    </el-input>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Search, Top } from '@element-plus/icons-vue'

interface Props {
  modelValue?: string
  placeholder?: string
  theme?: 'transparent' | 'dark'
}

interface Emits {
  (e: 'update:modelValue', value: string): void
  (e: 'search', value: string): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  placeholder: '',
  theme: 'transparent'
})

const emit = defineEmits<Emits>()

const searchValue = ref(props.modelValue)

const handleSearch = () => {
  emit('search', searchValue.value)
}

const handleInput = (value: string) => {
  emit('update:modelValue', value)
}

watch(() => props.modelValue, (newValue) => {
  searchValue.value = newValue
})
</script>

<style scoped>
.activity-search {
  width: 100%;
}

.search-input {
  --el-input-height: 40px;
}

/* 透明主题样式 */
.theme-transparent .search-input :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-full);
  padding: 0 var(--spacing-md);
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.theme-transparent .search-input :deep(.el-input__wrapper):hover {
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.15);
}

.theme-transparent .search-input :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.1);
}

/* 黑色主题样式 */
.theme-dark .search-input :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-full);
  padding: 0 var(--spacing-md);
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
  backdrop-filter: blur(20px);
}

.theme-dark .search-input :deep(.el-input__wrapper):hover {
  border-color: rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.2);
}

.theme-dark .search-input :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.25);
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.15);
}

/* 通用样式 */
.search-input :deep(.el-input__inner) {
  color: var(--text-color-inverse);
  font-size: var(--font-size-base);
  background: transparent;
}

.search-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.search-icon {
  color: rgba(255, 255, 255, 0.6);
  font-size: var(--font-size-lg);
}

.search-enter-btn {
  color: rgba(255, 255, 255, 0.8);
  background: transparent;
  border: none;
  padding: var(--spacing-xs);
  min-width: auto;
  height: 28px;
  width: 28px;
  border-radius: var(--border-radius-sm);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-enter-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--text-color-inverse);
}

.search-enter-btn .el-icon {
  transform: rotate(-90deg);
  font-size: var(--font-size-base);
}

/* 清除按钮样式 */
.search-input :deep(.el-input__suffix-inner .el-input__clear) {
  color: rgba(255, 255, 255, 0.6);
}

.search-input :deep(.el-input__suffix-inner .el-input__clear):hover {
  color: var(--text-color-inverse);
}
</style>
