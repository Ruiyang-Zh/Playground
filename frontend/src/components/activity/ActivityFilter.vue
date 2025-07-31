<template>
  <div class="activity-filter" :class="{ compact }">
    <div class="filter-grid" :class="{ 'compact-grid': compact }">
      <!-- 运动类型 -->
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

      <!-- 地区 -->
      <div class="filter-group">
        <label class="filter-label">地区</label>
        <el-cascader
          v-model="locationValue"
          :options="locationOptions"
          :props="{ expandTrigger: 'hover' }"
          placeholder="选择地区"
          clearable
          class="filter-select"
        />
      </div>

      <!-- 排序方式 -->
      <div class="filter-group">
        <label class="filter-label">排序</label>
        <div class="sort-controls">
          <el-select
            v-model="filterForm.sortBy"
            placeholder="排序方式"
            class="sort-select"
          >
            <el-option
              v-for="option in SORT_OPTIONS"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
          <el-select
            v-model="filterForm.sortDir"
            class="sort-dir-select"
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

      <!-- 时间范围 - 非紧凑模式 -->
      <div class="filter-group" v-if="!compact">
        <label class="filter-label">时间</label>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          class="filter-date"
        />
      </div>

      <!-- 费用范围 - 非紧凑模式 -->
      <div class="filter-group" v-if="!compact">
        <label class="filter-label">费用</label>
        <div class="fee-range">
          <el-input-number
            v-model="filterForm.minFee"
            :min="0"
            placeholder="最低"
            controls-position="right"
            class="fee-input"
          />
          <span class="fee-separator">-</span>
          <el-input-number
            v-model="filterForm.maxFee"
            :min="0"
            placeholder="最高"
            controls-position="right"
            class="fee-input"
          />
        </div>
      </div>

      <!-- 活动状态 - 非紧凑模式 -->
      <div class="filter-group" v-if="!compact">
        <label class="filter-label">状态</label>
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

    <!-- 操作按钮 -->
    <div class="filter-actions">
      <el-button @click="handleReset" class="reset-btn">
        重置
      </el-button>
      <el-button type="primary" @click="handleFilter" class="apply-btn">
        应用筛选
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import {
  SPORTS_OPTIONS,
  STATUS_OPTIONS,
  SORT_OPTIONS,
  SORT_DIR_OPTIONS
} from '@/utils/constants'
import type { ActivitySearchParams } from '@/types/activity'

interface Props {
  modelValue?: Partial<ActivitySearchParams>
  compact?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: Partial<ActivitySearchParams>): void
  (e: 'filter', value: Partial<ActivitySearchParams>): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({}),
  compact: false
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

const dateRange = ref<[string, string] | null>(null)
const locationValue = ref<string[]>([])

// 地区选项（简化版本）
const locationOptions = ref([
  {
    value: '北京市',
    label: '北京市',
    children: [
      { value: '东城区', label: '东城区' },
      { value: '西城区', label: '西城区' },
      { value: '朝阳区', label: '朝阳区' },
      { value: '海淀区', label: '海淀区' }
    ]
  },
  {
    value: '上海市',
    label: '上海市',
    children: [
      { value: '黄浦区', label: '黄浦区' },
      { value: '徐汇区', label: '徐汇区' },
      { value: '长宁区', label: '长宁区' },
      { value: '静安区', label: '静安区' }
    ]
  },
  {
    value: '广州市',
    label: '广州市',
    children: [
      { value: '天河区', label: '天河区' },
      { value: '越秀区', label: '越秀区' },
      { value: '海珠区', label: '海珠区' },
      { value: '荔湾区', label: '荔湾区' }
    ]
  },
  {
    value: '深圳市',
    label: '深圳市',
    children: [
      { value: '南山区', label: '南山区' },
      { value: '福田区', label: '福田区' },
      { value: '罗湖区', label: '罗湖区' },
      { value: '宝安区', label: '宝安区' }
    ]
  }
])

const handleFilter = () => {
  const params: Partial<ActivitySearchParams> = {
    ...filterForm.value
  }

  // 处理日期范围
  if (dateRange.value) {
    params.startTime = dateRange.value[0]
    params.endTime = dateRange.value[1]
  }

  // 处理地区选择
  if (locationValue.value.length > 0) {
    params.province = locationValue.value[0]
    params.city = locationValue.value[1]
    params.district = locationValue.value[2]
  }

  emit('update:modelValue', params)
  emit('filter', params)
}

const handleReset = () => {
  filterForm.value = {
    sortBy: 'startTime',
    sortDir: 'asc'
  }
  dateRange.value = null
  locationValue.value = []

  emit('update:modelValue', filterForm.value)
  emit('filter', filterForm.value)
}

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  Object.assign(filterForm.value, newValue)
}, { deep: true, immediate: true })
</script>

<style scoped>
.activity-filter {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
}

.activity-filter.compact {
  padding: var(--spacing-md);
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.filter-grid.compact-grid {
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.filter-label {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-color-secondary);
  margin: 0;
}

.filter-select,
.filter-date {
  width: 100%;
}

.sort-controls {
  display: flex;
  gap: var(--spacing-sm);
}

.sort-select {
  flex: 2;
}

.sort-dir-select {
  flex: 1;
}

.fee-range {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.fee-input {
  flex: 1;
}

.fee-separator {
  color: var(--text-color-secondary);
  font-size: var(--font-size-sm);
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-color-light);
}

.reset-btn {
  background: var(--bg-color-secondary);
  border: 1px solid var(--border-color);
  color: var(--text-color-secondary);
}

.reset-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.apply-btn {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.apply-btn:hover {
  background: var(--primary-light);
  border-color: var(--primary-light);
}

/* 紧凑模式特殊处理 */
@media (max-width: 768px) {
  .filter-grid.compact-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-sm);
  }

  .filter-actions {
    flex-direction: column;
  }

  .reset-btn,
  .apply-btn {
    width: 100%;
  }
}
</style>
