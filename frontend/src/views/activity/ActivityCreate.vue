<template>
  <div class="activity-create-page">
    <div class="create-container">
      <div class="create-header">
        <div class="header-content">
          <h1 class="page-title">创建活动</h1>
          <p class="page-subtitle">填写活动信息，让更多人参与你的运动</p>
        </div>
      </div>

      <div class="create-form-section">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="120px"
          class="create-form"
          label-position="top"
        >
          <!-- 基本信息 -->
          <div class="form-section">
            <h3 class="section-title">
              <el-icon class="section-icon"><InfoFilled /></el-icon>
              基本信息
            </h3>

            <div class="form-row">
              <el-form-item label="活动标题" prop="title" class="form-item-full">
                <el-input
                  v-model="form.title"
                  placeholder="请输入活动标题"
                  maxlength="50"
                  show-word-limit
                  class="form-input"
                />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="运动类型" prop="type" class="form-item-full">
                <el-select
                  v-model="form.type"
                  placeholder="选择运动类型"
                  class="form-input"
                >
                  <el-option
                    v-for="option in SPORTS_OPTIONS"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="活动描述" prop="description" class="form-item-full">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="4"
                  placeholder="详细描述你的活动..."
                  maxlength="1000"
                  show-word-limit
                  class="form-textarea"
                />
              </el-form-item>
            </div>
          </div>

          <!-- 时间地点 -->
          <div class="form-section">
            <h3 class="section-title">
              <el-icon class="section-icon"><Clock /></el-icon>
              时间地点
            </h3>

            <div class="form-row">
              <el-form-item label="开始时间" prop="startTime" class="form-item-half">
                <el-date-picker
                  v-model="form.startTime"
                  type="datetime"
                  placeholder="选择开始时间"
                  value-format="YYYY-MM-DD HH:00:00"
                  format="YYYY-MM-DD HH:00"
                  class="form-input"
                  :disabled-hours="getDisabledHours"
                />
              </el-form-item>

              <el-form-item label="结束时间" prop="endTime" class="form-item-half">
                <el-date-picker
                  v-model="form.endTime"
                  type="datetime"
                  placeholder="选择结束时间"
                  value-format="YYYY-MM-DD HH:00:00"
                  format="YYYY-MM-DD HH:00"
                  class="form-input"
                  :disabled-hours="getEndDisabledHours"
                />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="活动地区" prop="location" class="form-item-half">
                <el-cascader
                  v-model="locationValue"
                  :options="LOCATION_OPTIONS"
                  :props="{ expandTrigger: 'hover' }"
                  placeholder="选择地区"
                  class="form-input"
                  @change="handleLocationChange"
                  clearable
                />
              </el-form-item>

              <el-form-item label="具体地点" prop="venue" class="form-item-half">
                <el-input
                  v-model="form.venue"
                  placeholder="如：某某体育馆"
                  maxlength="100"
                  class="form-input"
                />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="报名截止时间" prop="registrationDeadline" class="form-item-full">
                <el-date-picker
                  v-model="form.registrationDeadline"
                  type="datetime"
                  placeholder="选择报名截止时间"
                  value-format="YYYY-MM-DD HH:00:00"
                  format="YYYY-MM-DD HH:00"
                  class="form-input"
                  :disabled-hours="getRegDeadlineDisabledHours"
                />
              </el-form-item>
            </div>
          </div>

          <!-- 参与设置 -->
          <div class="form-section">
            <h3 class="section-title">
              <el-icon class="section-icon"><User /></el-icon>
              参与设置
            </h3>

            <div class="form-row">
              <el-form-item label="最少参与人数" prop="minParticipants" class="form-item-half">
                <el-input-number
                  v-model="form.minParticipants"
                  :min="1"
                  :max="form.maxParticipants || 999"
                  placeholder="最少人数"
                  class="form-input"
                  controls-position="right"
                />
              </el-form-item>

              <el-form-item label="最多参与人数" prop="maxParticipants" class="form-item-half">
                <el-input-number
                  v-model="form.maxParticipants"
                  :min="form.minParticipants || 1"
                  :max="999"
                  placeholder="最多人数"
                  class="form-input"
                  controls-position="right"
                />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="参与要求" prop="requirements" class="form-item-full">
                <el-input
                  v-model="form.requirements"
                  type="textarea"
                  :rows="3"
                  placeholder="对参与者的要求或注意事项..."
                  maxlength="500"
                  show-word-limit
                  class="form-textarea"
                />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="联系方式" prop="contactInfo" class="form-item-full">
                <el-input
                  v-model="form.contactInfo"
                  placeholder="请输入联系方式（手机号、微信等）"
                  maxlength="100"
                  class="form-input"
                />
              </el-form-item>
            </div>
          </div>

          <!-- 活动图片 -->
          <div class="form-section">
            <h3 class="section-title">
              <el-icon class="section-icon"><Picture /></el-icon>
              活动图片
            </h3>

            <div class="form-row">
              <el-form-item label="上传图片" class="form-item-full">
                <el-upload
                  v-model:file-list="fileList"
                  :auto-upload="false"
                list-type="picture-card"
                accept="image/*"
                :limit="5"
                class="image-uploader"
                >
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <div class="upload-text">选择图片</div>
                </el-upload>
                <div class="upload-tip">
                  <el-icon><InfoFilled /></el-icon>
                  支持 JPG、PNG 格式，单张不超过 5MB，最多上传 5 张
                </div>
              </el-form-item>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="form-actions">
            <el-button
              size="large"
              @click="handleCancel"
              class="cancel-btn"
            >
              取消
            </el-button>
            <el-button
              type="primary"
              size="large"
              @click="handleSubmit"
              :loading="submitting"
              class="submit-btn"
            >
              <el-icon><Check /></el-icon>
              创建活动
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  InfoFilled,
  Clock,
  User,
  Picture,
  Plus,
  Check
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadProps, UploadUserFile } from 'element-plus'
import { useAuth } from '@/composables/useAuth'
import * as activityAPI from '@/api/activity'
import { SPORTS_OPTIONS, LOCATION_OPTIONS } from '@/utils/constants'
import type { SportsType } from '@/types/user'
import dayjs from 'dayjs'
import { formatDateTimeValue } from '@/utils/format.ts'

const router = useRouter()
const { currentUser } = useAuth()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const fileList = ref<UploadUserFile[]>([])
const locationValue = ref<string[]>([])

// 表单数据
const form = reactive({
  title: '',
  type: '' as SportsType | '',
  description: '',
  startTime: '',
  endTime: '',
  province: '',
  city: '',
  district: '',
  venue: '',
  minParticipants: 1,
  maxParticipants: 10,
  registrationDeadline: '',
  requirements: '',
  contactInfo: '',
  images: [] as string[]
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { min: 4, max: 50, message: '标题长度在 4 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择运动类型', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' },
    { min: 5, max: 1000, message: '描述长度在 5 到 1000 个字符', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value && dayjs(value).isBefore(dayjs().add(1, 'hour'))) {
          callback(new Error('开始时间不能早于当前时间1小时'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value && form.startTime && dayjs(value).isBefore(form.startTime)) {
          callback(new Error('结束时间不能早于开始时间'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  province: [
    { required: true, message: '请选择活动地区', trigger: 'change' }
  ],
  city: [
    { required: true, message: '请选择活动地区', trigger: 'change' }
  ],
  district: [
    { required: true, message: '请选择活动地区', trigger: 'change' }
  ],
  venue: [
    { required: true, message: '请输入具体地点', trigger: 'blur' }
  ],
  minParticipants: [
    { required: true, message: '请输入最少参与人数', trigger: 'blur' }
  ],
  maxParticipants: [
    { required: true, message: '请输入最多参与人数', trigger: 'blur' }
  ],
  registrationDeadline: [
    { required: true, message: '请选择报名截止时间', trigger: 'change' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value && form.startTime && dayjs(value).isAfter(form.startTime)) {
          callback(new Error('报名截止时间不能晚于活动开始时间'))
        } else if (value && dayjs(value).isBefore(dayjs())) {
          callback(new Error('报名截止时间不能早于当前时间'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  contactInfo: [
    { required: true, message: '请输入联系方式', trigger: 'blur' }
  ]
}

// 上传相关
const uploadAction = computed(() => '/api/upload/image')
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

// 时间限制函数
const getDisabledHours = () => {
  const now = dayjs()
  const currentHour = now.hour()
  const selectedDate = dayjs(form.startTime)

  // 如果选择的是今天，禁用当前小时之前的小时
  if (selectedDate.isSame(now, 'day')) {
    return Array.from({ length: currentHour + 1 }, (_, i) => i)
  }
  return []
}

const getEndDisabledHours = () => {
  if (!form.startTime) return []

  const startTime = dayjs(form.startTime)
  const selectedEndDate = dayjs(form.endTime)

  // 如果结束时间是同一天，禁用开始时间之前的小时
  if (selectedEndDate.isSame(startTime, 'day')) {
    const startHour = startTime.hour()
    return Array.from({ length: startHour + 1 }, (_, i) => i)
  }
  return []
}

const getRegDeadlineDisabledHours = () => {
  const now = dayjs()
  const currentHour = now.hour()
  const selectedDate = dayjs(form.registrationDeadline)

  // 如果选择的是今天，禁用当前小时之前的小时
  if (selectedDate.isSame(now, 'day')) {
    return Array.from({ length: currentHour + 1 }, (_, i) => i)
  }
  return []
}

const handleLocationChange = (value: string[]) => {
  if (value && value.length > 0) {
    form.province = value[0] || ''
    form.city = value[1] || ''
    form.district = value[2] || ''
  } else {
    form.province = ''
    form.city = ''
    form.district = ''
  }
}

const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const handleUploadSuccess = (response: any, file: UploadUserFile) => {
  if (response.code === 200) {
    form.images.push(response.data)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('图片上传失败，请重试')
}

const handleRemove = (file: UploadUserFile) => {
  // 从images数组中移除对应的图片URL
  const index = fileList.value.findIndex(f => f.uid === file.uid)
  if (index > -1 && form.images[index]) {
    form.images.splice(index, 1)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    submitting.value = true

    // 构建FormData
    const formData = new FormData()
    formData.append('title', form.title)
    formData.append('type', form.type)
    formData.append('description', form.description)
    formData.append('startTime', formatDateTimeValue(form.startTime))
    formData.append('endTime', formatDateTimeValue(form.endTime))
    formData.append('province', form.province)
    formData.append('city', form.city)
    formData.append('district', form.district)
    formData.append('venue', form.venue)
    formData.append('minParticipants', form.minParticipants.toString())
    formData.append('maxParticipants', form.maxParticipants.toString())
    formData.append('registrationDeadline', formatDateTimeValue(form.registrationDeadline))
    formData.append('requirements', form.requirements)
    formData.append('contactInfo', form.contactInfo)
    formData.append('fee', '0')

   // 添加图片文件
    fileList.value.forEach((file, index) => {
      if (file.raw) {
        formData.append('images', file.raw) // 使用同名字段 images，即后端接受 File[] 的格式
      }
    })


    const response = await activityAPI.createActivity(formData)

    ElMessage.success('活动创建成功！')
    router.push(`/activities/${response.data}`)
  } catch (error) {
    console.error('Create activity failed:', error)
    if (error !== false) {
      ElMessage.error('创建失败，请检查填写信息')
    }
  } finally {
    submitting.value = false
  }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消创建活动吗？已填写的信息将丢失。', '确认操作', {
      type: 'warning'
    })
    router.back()
  } catch (error) {
    // 用户取消操作
  }
}
</script>

<style scoped>
.activity-create-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--bg-color-secondary);
  padding: var(--spacing-xl) 0;
}

.create-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 var(--spacing-xl);
}

.create-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
}

.header-content {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-2xl);
  border: 1px solid var(--border-color-light);
  box-shadow: var(--shadow-sm);
}

.page-title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-extrabold);
  color: var(--text-color-primary);
  margin: 0 0 var(--spacing-md) 0;
}

.page-subtitle {
  font-size: var(--font-size-lg);
  color: var(--text-color-secondary);
  margin: 0;
}

.create-form-section {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  border: 1px solid var(--border-color-light);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.create-form {
  padding: var(--spacing-2xl);
}

.form-section {
  margin-bottom: var(--spacing-3xl);
  padding-bottom: var(--spacing-2xl);
  border-bottom: 1px solid var(--border-color-light);
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.section-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
  margin: 0 0 var(--spacing-xl) 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.section-icon {
  font-size: var(--font-size-lg);
  color: var(--primary-color);
}

.form-row {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.form-row:last-child {
  margin-bottom: 0;
}

.form-item-full {
  flex: 1;
}

.form-item-half {
  flex: 1;
}

.form-input,
.form-textarea {
  width: 100%;
}

.form-input :deep(.el-input__wrapper),
.form-textarea :deep(.el-textarea__inner) {
  border-radius: var(--border-radius-lg);
  border: 2px solid var(--border-color);
  transition: all 0.3s ease;
}

.form-input :deep(.el-input__wrapper):hover,
.form-textarea :deep(.el-textarea__inner):hover {
  border-color: var(--primary-color);
}

.form-input :deep(.el-input__wrapper.is-focus),
.form-textarea :deep(.el-textarea__inner):focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.image-uploader :deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
  border-radius: var(--border-radius-lg);
  border: 2px dashed var(--border-color);
  transition: all 0.3s ease;
}

.image-uploader :deep(.el-upload--picture-card):hover {
  border-color: var(--primary-color);
}

.upload-icon {
  font-size: var(--font-size-2xl);
  color: var(--text-color-tertiary);
  margin-bottom: var(--spacing-xs);
}

.upload-text {
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
}

.upload-tip {
  margin-top: var(--spacing-md);
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: var(--spacing-lg);
  padding-top: var(--spacing-2xl);
  border-top: 1px solid var(--border-color-light);
  margin-top: var(--spacing-2xl);
}

.cancel-btn,
.submit-btn {
  padding: var(--spacing-md) var(--spacing-2xl);
  border-radius: var(--border-radius-lg);
  font-weight: var(--font-weight-semibold);
  min-width: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
}

.cancel-btn {
  background: var(--bg-color-secondary);
  border: 2px solid var(--border-color);
  color: var(--text-color-secondary);
}

.cancel-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.submit-btn {
  background: var(--primary-color);
  border: 2px solid var(--primary-color);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.submit-btn:hover {
  background: var(--primary-light);
  border-color: var(--primary-light);
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .create-container {
    padding: 0 var(--spacing-lg);
  }

  .create-form {
    padding: var(--spacing-lg);
  }

  .form-row {
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .form-item-half {
    flex: none;
  }

  .form-actions {
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .cancel-btn,
  .submit-btn {
    width: 100%;
  }

  .page-title {
    font-size: var(--font-size-3xl);
  }
}

@media (max-width: 480px) {
  .activity-create-page {
    padding: var(--spacing-lg) 0;
  }

  .create-container {
    padding: 0 var(--spacing-md);
  }

  .header-content {
    padding: var(--spacing-lg);
  }

  .create-form {
    padding: var(--spacing-md);
  }

  .form-section {
    margin-bottom: var(--spacing-xl);
    padding-bottom: var(--spacing-xl);
  }
}
</style>
