<template>
  <div class="activity-filter" :class="{ compact }">
    <div class="filter-container">
      <!-- 第一行：运动类型 + 活动状态 -->
      <div class="filter-row">
        <div class="filter-group">
          <label class="filter-label">运动类型</label>
          <el-select
            v-model="filterForm.type"
            placeholder="全部类型"
            clearable
            class="filter-select"
          >
            <el-option
              v-for="option in SPORTS_OPTIONS"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </div>

        <div class="filter-group">
          <label class="filter-label">活动状态</label>
          <el-select
            v-model="filterForm.status"
            placeholder="全部状态"
            clearable
            class="filter-select"
          >
            <el-option
              v-for="option in STATUS_OPTIONS"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </div>
      </div>

      <!-- 第二行：地区 -->
      <div class="filter-row">
        <div class="filter-group adaptive-width">
          <label class="filter-label">地区</label>
          <el-cascader
            v-model="locationValue"
            :options="LOCATION_OPTIONS"
            :props="{ expandTrigger: 'hover' }"
            placeholder="选择地区"
            clearable
            class="filter-select"
          />
        </div>
      </div>

      <!-- 第三行：时间范围 -->
      <div class="filter-row">
        <div class="filter-group adaptive-width">
          <label class="filter-label">时间</label>
          <el-date-picker
            v-model="dateTimeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
            format="YYYY-MM-DD HH"
            class="filter-datetime"
            :shortcuts="dateTimeShortcuts"
          />
        </div>
      </div>

      <!-- 第四行：排序方式 -->
      <div class="filter-row">
        <div class="filter-group">
          <label class="filter-label">排序字段</label>
          <el-select
            v-model="filterForm.sortBy"
            placeholder="排序字段"
            class="filter-select"
          >
            <el-option
              v-for="option in SORT_OPTIONS"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </div>

        <div class="filter-group">
          <label class="filter-label">排序方向</label>
          <el-select
            v-model="filterForm.sortDir"
            class="filter-select"
          >
            <el-option
              v-for="option in SORT_DIR_OPTIONS"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="filter-actions">
      <el-button @click="handleReset" class="reset-btn">
        <el-icon><RefreshLeft /></el-icon>
        重置
      </el-button>
      <el-button type="primary" @click="handleFilter" class="apply-btn">
        <el-icon><Search /></el-icon>
        应用筛选
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { RefreshLeft, Search } from '@element-plus/icons-vue'
import {
  SPORTS_OPTIONS,
  STATUS_OPTIONS,
  SORT_OPTIONS,
  SORT_DIR_OPTIONS,
  LOCATION_OPTIONS
} from '@/utils/constants'
import type { ActivitySearchParams } from '@/types/activity'

interface Props {
  modelValue?: Partial<ActivitySearchParams>
  compact?: boolean
  showFeeFilter?: boolean // 控制是否显示费用筛选
}

interface Emits {
  (e: 'update:modelValue', value: Partial<ActivitySearchParams>): void
  (e: 'filter', value: Partial<ActivitySearchParams>): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({}),
  compact: false,
  showFeeFilter: true
})

const emit = defineEmits<Emits>()

const filterForm = ref<Partial<ActivitySearchParams>>({
  type: undefined,
  status: undefined,
  minFee: undefined,
  maxFee: undefined,
  province: undefined,
  city: undefined,
  district: undefined,
  sortBy: 'startTime',
  sortDir: 'asc'
})

const dateTimeRange = ref<[string, string] | null>(null)
const locationValue = ref<string[]>([])

// 日期时间快捷选项（精确到小时）
const dateTimeShortcuts = [
  {
    text: '今天上午',
    value: () => {
      const today = new Date()
      const start = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 9, 0, 0)
      const end = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 12, 0, 0)
      return [start, end]
    }
  },
  {
    text: '今天下午',
    value: () => {
      const today = new Date()
      const start = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 14, 0, 0)
      const end = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 18, 0, 0)
      return [start, end]
    }
  },
  {
    text: '今天全天',
    value: () => {
      const today = new Date()
      const start = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 0, 0, 0)
      const end = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59)
      return [start, end]
    }
  },
  {
    text: '明天全天',
    value: () => {
      const tomorrow = new Date()
      tomorrow.setDate(tomorrow.getDate() + 1)
      const start = new Date(tomorrow.getFullYear(), tomorrow.getMonth(), tomorrow.getDate(), 0, 0, 0)
      const end = new Date(tomorrow.getFullYear(), tomorrow.getMonth(), tomorrow.getDate(), 23, 59, 59)
      return [start, end]
    }
  },
  {
    text: '本周末',
    value: () => {
      const now = new Date()
      const saturday = new Date()
      const sunday = new Date()

      // 计算本周六
      const daysUntilSaturday = 6 - now.getDay()
      saturday.setDate(now.getDate() + daysUntilSaturday)
      saturday.setHours(0, 0, 0, 0)

      // 计算本周日
      sunday.setDate(saturday.getDate() + 1)
      sunday.setHours(23, 59, 59, 999)

      return [saturday, sunday]
    }
  },
  {
    text: '本周工作日',
    value: () => {
      const now = new Date()
      const monday = new Date()
      const friday = new Date()

      // 计算本周一
      const daysUntilMonday = now.getDay() === 0 ? 1 : 1 - now.getDay()
      monday.setDate(now.getDate() + daysUntilMonday)
      monday.setHours(9, 0, 0, 0)

      // 计算本周五
      friday.setDate(monday.getDate() + 4)
      friday.setHours(18, 0, 0, 0)

      return [monday, friday]
    }
  }
]

const handleFilter = () => {
  const params: Partial<ActivitySearchParams> = {
    ...filterForm.value
  }

  // 处理日期时间范围
  if (dateTimeRange.value) {
    params.startTime = dateTimeRange.value[0]
    params.endTime = dateTimeRange.value[1]
  } else {
    delete params.startTime
    delete params.endTime
  }

  // 处理地区选择
  if (locationValue.value.length > 0) {
    params.province = locationValue.value[0]
    if (locationValue.value.length > 1) {
      params.city = locationValue.value[1]
    }
    if (locationValue.value.length > 2) {
      params.district = locationValue.value[2]
    }
  } else {
    delete params.province
    delete params.city
    delete params.district
  }

  // 清理空值
  Object.keys(params).forEach(key => {
    if (params[key] === undefined || params[key] === '') {
      delete params[key]
    }
  })

  emit('update:modelValue', params)
  emit('filter', params)
}

const handleReset = () => {
  filterForm.value = {
    sortBy: 'startTime',
    sortDir: 'asc'
  }
  dateTimeRange.value = null
  locationValue.value = []

  const params = {
    sortBy: 'startTime',
    sortDir: 'asc'
  }

  emit('update:modelValue', params)
  emit('filter', params)
}

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  Object.assign(filterForm.value, newValue)

  // 处理日期时间范围
  if (newValue.startTime && newValue.endTime) {
    dateTimeRange.value = [newValue.startTime, newValue.endTime]
  } else {
    dateTimeRange.value = null
  }

  // 处理地区
  locationValue.value = []
  if (newValue.province) {
    locationValue.value.push(newValue.province)
    if (newValue.city) {
      locationValue.value.push(newValue.city)
      if (newValue.district) {
        locationValue.value.push(newValue.district)
      }
    }
  }
}, { deep: true, immediate: true })
</script>

<style scoped>
.activity-filter {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-xl);
  border: 1px solid var(--border-color-light);
  box-shadow: var(--shadow-sm);
  width: 100%;
  box-sizing: border-box;
}

.activity-filter.compact {
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
}

.filter-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
  width: 100%;
}

.filter-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-xl);
  align-items: end;
  width: 100%;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  width: 100%;
  min-width: 0; /* 允许元素收缩 */
}

/* 自适应宽度的组件占满整行 */
.filter-group.adaptive-width {
  grid-column: 1 / -1;
  max-width: 600px; /* 设置合理的最大宽度 */
}

.filter-label {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  flex-shrink: 0;
}

.filter-select,
.filter-datetime {
  width: 100%;
  --el-select-height: 44px;
  --el-input-height: 44px;
  min-width: 0; /* 允许组件收缩 */
  flex: 1;
}

.filter-select :deep(.el-input__wrapper),
.filter-datetime :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-lg);
  border: 2px solid var(--border-color);
  transition: all 0.3s ease;
  width: 100%;
  min-width: 0;
}

.filter-select :deep(.el-input__wrapper):hover,
.filter-datetime :deep(.el-input__wrapper):hover {
  border-color: var(--primary-color);
}

.filter-select :deep(.el-input__wrapper.is-focus),
.filter-datetime :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

/* 确保时间选择器的内部输入框也能自适应 */
.filter-datetime :deep(.el-input__inner) {
  width: 100%;
  min-width: 0;
}

.filter-datetime :deep(.el-range-input) {
  flex: 1;
  min-width: 0;
}

.filter-datetime :deep(.el-range__close-icon),
.filter-datetime :deep(.el-range__icon) {
  flex-shrink: 0;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-lg);
  padding-top: var(--spacing-xl);
  border-top: 2px solid var(--border-color-light);
  width: 100%;
  flex-wrap: wrap; /* 允许按钮换行 */
}

.reset-btn,
.apply-btn {
  padding: var(--spacing-md) var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-base);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 120px;
  justify-content: center;
  flex-shrink: 0;
}

.reset-btn {
  background: var(--bg-color-secondary);
  border: 2px solid var(--border-color);
  color: var(--text-color-secondary);
}

.reset-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(64, 158, 255, 0.05);
  transform: translateY(-1px);
}

.apply-btn {
  background: var(--primary-color);
  border: 2px solid var(--primary-color);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.apply-btn:hover {
  background: var(--primary-light);
  border-color: var(--primary-light);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

/* 紧凑模式 */
.activity-filter.compact .filter-container {
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.activity-filter.compact .filter-row {
  gap: var(--spacing-lg);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .filter-row {
    gap: var(--spacing-lg);
  }

  .filter-group.adaptive-width {
    max-width: 500px;
  }
}

@media (max-width: 768px) {
  .filter-row {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }

  .filter-group.adaptive-width {
    grid-column: 1;
    max-width: none; /* 移动端移除最大宽度限制 */
  }

  .filter-container {
    gap: var(--spacing-lg);
  }

  .filter-actions {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: stretch;
  }

  .reset-btn,
  .apply-btn {
    width: 100%;
    min-width: auto;
  }
}

@media (max-width: 480px) {
  .activity-filter {
    padding: var(--spacing-lg);
  }

  .activity-filter.compact {
    padding: var(--spacing-md);
  }

  .filter-container {
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);
  }

  .filter-row {
    gap: var(--spacing-md);
  }

  .filter-actions {
    gap: var(--spacing-sm);
  }
}

/* 确保在所有屏幕尺寸下都能正常显示 */
@media (max-width: 320px) {
  .filter-datetime :deep(.el-input__wrapper) {
    min-width: 280px;
  }
}
</style>
