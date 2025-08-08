<template>
  <div class="profile-page">
    <!-- 个人信息展示区域 -->
    <section class="profile-section">
      <div class="profile-background">
        <div class="profile-gradient"></div>
      </div>

      <div class="profile-content">
        <!-- 编辑按钮区域 - 只有查看自己的资料时显示 -->
        <div v-if="isOwnProfile" class="edit-buttons-corner">
          <el-button
            type="primary"
            @click="showEditDialog = true"
            class="edit-btn"
          >
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
          <el-button
            type="warning"
            @click="showPasswordDialog = true"
            class="password-btn"
          >
            <el-icon><Lock /></el-icon>
            修改密码
          </el-button>
        </div>

        <!-- 用户基本信息区域 -->
        <div class="profile-info-card">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <div class="avatar-frame">
              <UserAvatar
                :user="displayUser"
                size="extra-large"
                :show-hover="false"
                class="profile-avatar"
              />
            </div>
            <div class="user-basic-info">
              <h1 class="username">{{ displayUser?.username || '运动爱好者' }}</h1>

              <!-- 用户详细信息卡片 -->
              <div class="user-info-card">
                <div class="info-item">
                  <span class="info-label">用户ID</span>
                  <span class="info-value">{{ displayUser?.id || '-' }}</span>
                </div>
                <!-- 只有查看自己的资料时显示手机号和邮箱 -->
                <template v-if="isOwnProfile && displayUser">
                  <div class="info-item" v-if="displayUser.phone">
                    <span class="info-label">手机号</span>
                    <span class="info-value">{{ displayUser.phone }}</span>
                  </div>
                  <div class="info-item" v-if="displayUser.email">
                    <span class="info-label">邮箱</span>
                    <span class="info-value">{{ displayUser.email }}</span>
                  </div>
                </template>
              </div>

              <p class="user-description">
                {{ displayUser?.description || '暂未添加个人介绍' }}
              </p>
            </div>
          </div>

          <div class="divider-vertical"></div>

          <!-- 详细信息区域 -->
          <div class="user-details-section">
            <!-- 运动偏好 -->
            <div class="info-section sports-preferences-section" v-if="displayUser?.sportsPreference?.length">
              <h3 class="section-title">
                <el-icon class="section-icon"><Star /></el-icon>
                运动偏好
              </h3>
              <div class="preferences-list">
                <el-tag
                  v-for="sport in displayUser.sportsPreference"
                  :key="sport"
                  type="info"
                  class="sport-tag"
                >
                  {{ getActivityTypeText(sport) }}
                </el-tag>
              </div>
            </div>

            <div class="divider-horizontal" v-if="displayUser?.sportsPreference?.length"></div>

            <!-- 活动统计 -->
            <div class="info-section user-stats-section">
              <h3 class="section-title">
                <el-icon class="section-icon"><TrendCharts /></el-icon>
                活动统计
              </h3>
              <div class="user-stats">
                <div class="stat-item">
                  <div class="stat-content">
                    <span class="stat-number">{{ joinedActivities.length }}</span>
                    <span class="stat-label">参与活动</span>
                  </div>
                </div>
                <div class="stat-item">
                  <div class="stat-content">
                    <span class="stat-number">{{ createdActivities.length }}</span>
                    <span class="stat-label">组织活动</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 下拉提示 -->
        <div class="scroll-indicator" @click="scrollToActivities">
          <el-icon class="scroll-arrow"><ArrowDown /></el-icon>
          <span class="scroll-text">查看活动记录</span>
        </div>
      </div>
    </section>

    <!-- 活动记录区域 -->
    <section class="activities-section" ref="activitiesSection">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">活动记录</h2>
          <!-- 只有查看自己的资料时显示管理提示 -->
          <p v-if="isOwnProfile" class="section-subtitle">管理您的所有活动</p>
          <p v-else class="section-subtitle">{{ displayUser?.username }} 的活动记录</p>
        </div>

        <!-- 活动类型切换标签 -->
        <div class="activities-controls">
          <div class="filter-tabs">
            <el-radio-group
              v-model="activeTab"
              @change="handleTabChange"
              class="tab-group"
            >
              <el-radio-button value="joined">参与的活动 ({{ joinedActivities.length }})</el-radio-button>
              <el-radio-button value="created">组织的活动 ({{ createdActivities.length }})</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 只有查看自己的资料时显示创建按钮 -->
          <div v-if="isOwnProfile" class="action-buttons">
            <el-button
              type="primary"
              @click="$router.push('/activities/create')"
              class="create-btn"
            >
              <el-icon><Plus /></el-icon>
              创建活动
            </el-button>
          </div>
        </div>

        <!-- 活动列表 -->
        <div class="activities-container">
          <div v-if="initialLoading" class="loading-state">
            <div class="loading-grid">
              <el-skeleton
                v-for="i in 8"
                :key="i"
                animated
                class="activity-skeleton"
              >
                <template #template>
                  <el-skeleton-item variant="image" style="width: 100%; height: 200px;" />
                  <div style="padding: 14px;">
                    <el-skeleton-item variant="h3" style="width: 80%;" />
                    <el-skeleton-item variant="text" style="width: 60%; margin-top: 12px;" />
                    <el-skeleton-item variant="text" style="width: 40%; margin-top: 8px;" />
                  </div>
                </template>
              </el-skeleton>
            </div>
          </div>

          <div v-else-if="currentActivities.length === 0" class="empty-state">
            <el-icon class="empty-icon"><Calendar /></el-icon>
            <h3 class="empty-title">{{ getEmptyText() }}</h3>
            <p class="empty-text">{{ getEmptyDescription() }}</p>
            <!-- 只有查看自己的资料且是创建的活动tab时显示创建按钮 -->
            <el-button
              v-if="activeTab === 'created' && isOwnProfile"
              type="primary"
              @click="$router.push('/activities/create')"
              class="create-activity-btn"
            >
              创建第一个活动
            </el-button>
          </div>

          <div v-else class="activities-grid">
            <ActivityCard
              v-for="activity in currentActivities"
              :key="activity.id"
              :activity="activity"
              @register="handleRegister"
              @unregister="handleUnregister"
              @view-detail="handleViewDetail"
              class="activity-card-item"
            />
          </div>

          <!-- 加载更多 -->
          <div v-if="hasMore && currentActivities.length > 0" class="load-more-section">
            <el-button
              @click="loadMore"
              :loading="loadingMore"
              size="large"
              class="load-more-btn"
            >
              加载更多
            </el-button>
          </div>

          <!-- 加载中状态 -->
          <div v-if="loadingMore && currentActivities.length > 0" class="loading-more">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 编辑相关对话框只有查看自己资料时显示 -->
    <template v-if="isOwnProfile">
      <!-- 编辑资料对话框 -->
      <el-dialog
        v-model="showEditDialog"
        title="编辑个人资料"
        width="500px"
        :before-close="handleEditClose"
      >
        <el-form
          ref="editFormRef"
          :model="editForm"
          :rules="editRules"
          label-width="80px"
          class="edit-form"
        >
          <!-- 头像上传 -->
          <el-form-item label="头像">
            <el-upload
              ref="avatarUploadRef"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleAvatarChange"
              accept="image/*"
              class="avatar-uploader"
            >
              <div class="avatar-upload-content">
                <img
                  v-if="avatarPreview || currentUser?.avatar"
                  :src="avatarPreview || currentUser?.avatar"
                  class="avatar-preview"
                  alt="头像预览"
                />
                <div v-else class="avatar-placeholder">
                  <el-icon class="avatar-upload-icon"><Plus /></el-icon>
                  <div class="avatar-upload-text">上传头像</div>
                </div>
              </div>
            </el-upload>
          </el-form-item>

          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="editForm.username"
              placeholder="请输入用户名"
              maxlength="20"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="editForm.phone"
              placeholder="请输入手机号"
              maxlength="11"
            />
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="editForm.email"
              placeholder="请输入邮箱"
              type="email"
            />
          </el-form-item>

          <el-form-item label="个人简介" prop="description">
            <el-input
              v-model="editForm.description"
              type="textarea"
              :rows="4"
              placeholder="介绍一下自己吧"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="运动偏好" prop="sportsPreference">
            <el-select
              v-model="editForm.sportsPreference"
              multiple
              placeholder="选择您喜欢的运动"
              style="width: 100%"
            >
              <el-option
                v-for="sport in SPORTS_OPTIONS"
                :key="sport.value"
                :label="sport.label"
                :value="sport.value"
              />
            </el-select>
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="handleEditCancel">取消</el-button>
            <el-button
              type="primary"
              @click="handleSaveProfile"
              :loading="saving"
            >
              保存
            </el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 修改密码对话框 -->
      <el-dialog
        v-model="showPasswordDialog"
        title="修改密码"
        width="400px"
        :before-close="handlePasswordClose"
      >
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="80px"
          class="password-form"
        >
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入当前密码"
              show-password
              autocomplete="current-password"
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
              autocomplete="new-password"
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
              autocomplete="new-password"
            />
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="handlePasswordCancel">取消</el-button>
            <el-button
              type="primary"
              @click="handleChangePassword"
              :loading="changingPassword"
            >
              确认修改
            </el-button>
          </div>
        </template>
      </el-dialog>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowDown,
  Calendar,
  Loading,
  Plus,
  Edit,
  Star,
  TrendCharts,
  Lock
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { UploadFile } from 'element-plus'
import { useAuth } from '@/composables/useAuth'
import { useActivity } from '@/composables/useActivity'
import { useUserInfo } from '@/composables/useUserInfo'
import UserAvatar from '@/components/common/UserAvatar.vue'
import ActivityCard from '@/components/activity/ActivityCard.vue'
import { SPORTS_OPTIONS } from '@/utils/constants'
import { getActivityTypeText } from '@/utils/format'
import type { SportsType, UserPublicInfoResponse } from '@/types/user'
import type { ActivityBriefResponse } from '@/types/activity'

const route = useRoute()
const router = useRouter()
const { currentUser } = useAuth()
const { fetchUserActivities, registerActivity, unregisterActivity } = useActivity()
const { updateUserInfo, updatePassword, fetchUserInfo } = useUserInfo()

// 当前查看的用户信息
const viewingUser = ref<UserPublicInfoResponse | null>(null)
const isOwnProfile = computed(() => {
  const userId = route.params.userId
  return !userId || (currentUser.value && userId == currentUser.value.id)
})

// 显示的用户信息
const displayUser = computed(() => {
  return isOwnProfile.value ? currentUser.value : viewingUser.value
})

// 响应式数据
const activitiesSection = ref<HTMLElement>()
const editFormRef = ref()
const avatarUploadRef = ref()
const activeTab = ref<'joined' | 'created'>('joined')
const showEditDialog = ref(false)
const saving = ref(false)
const initialLoading = ref(true)
const loadingMore = ref(false)
const showPasswordDialog = ref(false)
const changingPassword = ref(false)
const passwordFormRef = ref()

// 活动数据
const joinedActivities = ref<ActivityBriefResponse[]>([])
const createdActivities = ref<ActivityBriefResponse[]>([])
const joinedPagination = ref({ page: 0, size: 12, totalPages: 1 })
const createdPagination = ref({ page: 0, size: 12, totalPages: 1 })

// 编辑表单
const editForm = ref({
  username: '',
  phone: '',
  email: '',
  description: '',
  sportsPreference: [] as SportsType[]
})

// 修改密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 头像相关
const avatarFile = ref<File | null>(null)
const avatarPreview = ref<string>('')

// 表单验证规则
const editRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ],
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱地址',
      trigger: 'blur'
    }
  ]
}

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取用户信息的函数
const loadUserProfile = async () => {
  const userId = route.params.userId
  if (userId && userId != currentUser.value?.id) {
    try {
      const user = await fetchUserInfo(Number(userId))
      viewingUser.value = user
    } catch (error) {
      console.error('获取用户信息失败:', error)
      ElMessage.error('用户不存在或获取失败')
    }
  }
}

// 修改密码相关函数
const handlePasswordCancel = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  showPasswordDialog.value = false
}

const handlePasswordClose = (done: () => void) => {
  if (changingPassword.value) {
    return
  }
  done()
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value?.validate()
    changingPassword.value = true

    await updatePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })

    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false

    // 重置表单
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }

  } catch (error) {
    if (error !== false) {
      ElMessage.error('密码修改失败，请检查当前密码是否正确')
    }
  } finally {
    changingPassword.value = false
  }
}

// 计算属性
const currentActivities = computed(() => {
  return activeTab.value === 'joined' ? joinedActivities.value : createdActivities.value
})

const currentPagination = computed(() => {
  return activeTab.value === 'joined' ? joinedPagination.value : createdPagination.value
})

const hasMore = computed(() => {
  return currentPagination.value.page < currentPagination.value.totalPages - 1
})

// 修改空状态文本函数
const getEmptyText = () => {
  const prefix = isOwnProfile.value ? '您' : `${displayUser.value?.username}`
  return activeTab.value === 'joined'
    ? `${prefix}还没有参与任何活动`
    : `${prefix}还没有组织任何活动`
}

const getEmptyDescription = () => {
  if (!isOwnProfile.value) {
    return activeTab.value === 'joined'
      ? 'TA还没有参与任何活动'
      : 'TA还没有组织任何活动'
  }

  return activeTab.value === 'joined'
    ? '快去发现和参与感兴趣的活动吧'
    : '创建您的第一个活动，开始组织运动'
}

// 头像处理
const handleAvatarChange = (file: UploadFile) => {
  if (file.raw) {
    avatarFile.value = file.raw
    // 创建预览URL
    const reader = new FileReader()
    reader.onload = (e) => {
      avatarPreview.value = e.target?.result as string
    }
    reader.readAsDataURL(file.raw)
  }
}

// 事件处理函数
const handleTabChange = (value: string) => {
  activeTab.value = value as 'joined' | 'created'
}

const fetchActivitiesData = async (type: 'joined' | 'created', reset = false) => {
  try {
    const participationType = type === 'joined' ? 'JOINED' : 'CREATED'
    const pagination = type === 'joined' ? joinedPagination.value : createdPagination.value

    const data = await fetchUserActivities(Number(route.params.userId), participationType)

    if (data) {
      const activities = data.content || []
      if (reset) {
        if (type === 'joined') {
          joinedActivities.value = activities
          joinedPagination.value = {
            page: data.page || 0,
            size: data.size || 12,
            totalPages: data.totalPages || 1
          }
        } else {
          createdActivities.value = activities
          createdPagination.value = {
            page: data.page || 0,
            size: data.size || 12,
            totalPages: data.totalPages || 1
          }
        }
      } else {
        // 加载更多
        if (type === 'joined') {
          joinedActivities.value.push(...activities)
          joinedPagination.value.page = data.page || pagination.page + 1
        } else {
          createdActivities.value.push(...activities)
          createdPagination.value.page = data.page || pagination.page + 1
        }
      }
    }
  } catch (error) {
    console.error(`获取${type === 'joined' ? '参与' : '组织'}的活动失败:`, error)
    ElMessage.error(`获取${type === 'joined' ? '参与' : '组织'}的活动失败`)
  }
}

const loadInitialData = async () => {
  try {
    initialLoading.value = true

    // 并行获取参与的活动和组织的活动
    await Promise.all([
      fetchActivitiesData('joined', true),
      fetchActivitiesData('created', true)
    ])
  } catch (error) {
    console.error('初始化数据失败:', error)
  } finally {
    initialLoading.value = false
  }
}

const loadMore = async () => {
  if (loadingMore.value || !hasMore.value) return

  loadingMore.value = true
  try {
    await fetchActivitiesData(activeTab.value, false)
  } finally {
    loadingMore.value = false
  }
}

const handleRegister = async (activityId: number) => {
  try {
    await registerActivity(activityId)
    // 重新加载当前标签页的数据
    await fetchActivitiesData(activeTab.value, true)
  } catch (error) {
    console.error('注册活动失败:', error)
  }
}

const handleUnregister = async (activityId: number) => {
  try {
    await unregisterActivity(activityId)
    // 重新加载当前标签页的数据
    await fetchActivitiesData(activeTab.value, true)
  } catch (error) {
    console.error('取消报名失败:', error)
  }
}

const handleViewDetail = (activityId: number) => {
  window.open(`/activities/${activityId}`, '_blank')
}

const scrollToActivities = () => {
  nextTick(() => {
    activitiesSection.value?.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    })
  })
}

const handleEditClose = (done: () => void) => {
  if (saving.value) {
    return
  }
  done()
}

const handleEditCancel = () => {
  // 重置表单和预览
  if (currentUser.value) {
    editForm.value = {
      username: currentUser.value.username || '',
      phone: currentUser.value.phone || '',
      email: currentUser.value.email || '',
      description: currentUser.value.description || '',
      sportsPreference: currentUser.value.sportsPreference || []
    }
  }
  avatarFile.value = null
  avatarPreview.value = ''
  showEditDialog.value = false
}

const handleSaveProfile = async () => {
  try {
    await editFormRef.value?.validate()
    saving.value = true

    // 创建FormData
    const formData = new FormData()
    formData.append('username', editForm.value.username)
    formData.append('phone', editForm.value.phone || '')
    formData.append('email', editForm.value.email || '')
    formData.append('description', editForm.value.description || '')

    // 处理运动偏好
    if (editForm.value.sportsPreference?.length) {
      editForm.value.sportsPreference.forEach(sport => {
        formData.append('sportsPreference', sport)
      })
    }

    // 如果有头像文件，添加到FormData
    if (avatarFile.value) {
      formData.append('avatar', avatarFile.value)
    }

    // 调用API更新用户信息
    const updatedUser = await updateUserInfo(formData)

    ElMessage.success('个人资料更新成功')
    showEditDialog.value = false

    // 重置头像相关状态
    avatarFile.value = null
    avatarPreview.value = ''

  } catch (error) {
    if (error !== false) {
      ElMessage.error('保存失败，请重试')
    }
  } finally {
    saving.value = false
  }
}

// 监听路由查询参数，决定初始标签页
watch(() => route.query.tab, (tab) => {
  if (tab === 'activities') {
    nextTick(() => {
      scrollToActivities()
    })
  }
}, { immediate: true })

// 监听路由变化
watch(() => route.params.userId, async () => {
  await loadUserProfile()
  await loadInitialData()
})

onMounted(async () => {
  // 加载用户资料
  await loadUserProfile()

  // 只有查看自己的资料时才初始化编辑表单
  if (isOwnProfile.value && currentUser.value) {
    editForm.value = {
      username: currentUser.value.username || '',
      phone: currentUser.value.phone || '',
      email: currentUser.value.email || '',
      description: currentUser.value.description || '',
      sportsPreference: currentUser.value.sportsPreference || []
    }
  }

  // 获取活动数据
  await loadInitialData()

  // 如果URL包含activities参数，自动滚动到活动区域
  if (route.query.tab === 'activities') {
    setTimeout(() => {
      scrollToActivities()
    }, 500)
  }
})
</script>

<style scoped>
.profile-page {
  width: 100%;
  min-height: 100vh;
}

/* 个人信息区域样式 */
.profile-section {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin-left: calc(-50vw + 50%);
}

.profile-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.profile-gradient {
  width: 100%;
  height: 100%;
  background: var(--gradient-user-bg);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.profile-content {
  position: relative;
  z-index: 10;
  color: var(--text-color-inverse);
  padding: var(--spacing-xl);
  max-width: 1200px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

/* 编辑按钮区域 */
.edit-buttons-corner {
  position: absolute;
  top: calc(var(--header-height) + var(--spacing-lg));
  right: var(--spacing-lg);
  z-index: 20;
  display: flex;
  gap: var(--spacing-md);
}

.edit-btn,
.password-btn {
  border-radius: var(--border-radius-full);
  backdrop-filter: blur(10px);
  color: var(--text-color-inverse);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: auto;
  padding: var(--spacing-sm) var(--spacing-lg);
}

.edit-btn {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.edit-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.password-btn {
  background: rgba(245, 108, 108, 0.15);
  border: 1px solid rgba(245, 108, 108, 0.3);
}

.password-btn:hover {
  background: rgba(245, 108, 108, 0.25);
  border-color: rgba(245, 108, 108, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 108, 108, 0.15);
}

.profile-info-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: var(--border-radius-2xl);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: var(--spacing-3xl);
  display: flex;
  gap: var(--spacing-3xl);
  align-items: stretch;
  width: 100%;
  max-width: 1000px;
  margin-bottom: var(--spacing-3xl);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

/* 头像区域重新设计 */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xl);
  text-align: center;
  flex-shrink: 0;
  min-width: 320px;
}

.avatar-frame {
  position: relative;
  padding: var(--spacing-lg);
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

.profile-avatar {
  border: 6px solid rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.user-basic-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  width: 100%;
}

.username {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-extrabold);
  margin: 0;
  color: var(--text-color-inverse);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: 0.5px;
  line-height: 1.2;
}

/* 用户信息卡片样式 */
.user-info-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-md);
  margin: var(--spacing-md) 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-xs) 0;
}

.info-label {
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.7);
  font-weight: var(--font-weight-medium);
}

.info-value {
  font-size: var(--font-size-sm);
  color: var(--text-color-inverse);
  font-weight: var(--font-weight-semibold);
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Fira Code', monospace;
}

.user-description {
  font-size: var(--font-size-lg);
  margin: 0;
  opacity: 0.9;
  line-height: 1.6;
  color: var(--text-color-inverse);
  font-weight: var(--font-weight-medium);
}

.divider-vertical {
  width: 2px;
  background: linear-gradient(
    to bottom,
    transparent,
    rgba(255, 255, 255, 0.3) 20%,
    rgba(255, 255, 255, 0.3) 80%,
    transparent
  );
  margin: var(--spacing-lg) 0;
  border-radius: 1px;
}

.divider-horizontal {
  height: 2px;
  background: linear-gradient(
    to right,
    transparent,
    rgba(255, 255, 255, 0.3) 20%,
    rgba(255, 255, 255, 0.3) 80%,
    transparent
  );
  margin: var(--spacing-2xl) 0;
  border-radius: 1px;
}

/* 详细信息区域 */
.user-details-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2xl);
  min-width: 0;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.section-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  margin: 0;
  color: var(--text-color-inverse);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.section-icon {
  font-size: var(--font-size-xl);
  opacity: 0.8;
}

/* 运动偏好区域 */
.preferences-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.sport-tag {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: var(--text-color-inverse);
  backdrop-filter: blur(10px);
  padding: var(--spacing-sm) var(--spacing-lg);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-base);
  border-radius: var(--border-radius-full);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.sport-tag:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

/* 统计区域 */
.user-stats {
  display: flex;
  gap: var(--spacing-2xl);
}

.stat-item {
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--border-radius-xl);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: var(--spacing-xl);
  flex: 1;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.stat-number {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-extrabold);
  color: var(--text-color-inverse);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  line-height: 1;
}

.stat-label {
  font-size: var(--font-size-base);
  opacity: 0.9;
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-inverse);
  text-align: center;
}

.scroll-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  transition: var(--transition-base);
  position: absolute;
  bottom: var(--spacing-2xl);
}

.scroll-indicator:hover {
  transform: translateY(-5px);
}

.scroll-arrow {
  font-size: var(--font-size-2xl);
  animation: bounce 2s infinite;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.scroll-text {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  opacity: 0.9;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

/* 活动记录区域样式 */
.activities-section {
  background: var(--bg-color-primary);
  padding: var(--spacing-3xl) 0;
  min-height: 100vh;
}

.section-container {
  max-width: var(--container-max-width);
  margin: 0 auto;
  padding: 0 var(--spacing-xl);
  width: 100%;
}

.section-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
}

.section-header .section-title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-extrabold);
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-color-primary);
  text-shadow: none;
  display: block;
}

.section-subtitle {
  font-size: var(--font-size-lg);
  color: var(--text-color-secondary);
  margin: 0;
}

.activities-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  gap: var(--spacing-lg);
}

.tab-group {
  flex: 1;
}

.tab-group :deep(.el-radio-button__inner) {
  border-radius: var(--border-radius-md);
  font-weight: var(--font-weight-medium);
  padding: var(--spacing-sm) var(--spacing-lg);
}

.action-buttons {
  display: flex;
  gap: var(--spacing-md);
}

.create-btn {
  border-radius: var(--border-radius-md);
  font-weight: var(--font-weight-medium);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-lg);
}

.activities-container {
  margin-top: var(--spacing-xl);
}

.loading-state {
  padding: var(--spacing-xl) 0;
}

.loading-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-xl);
}

.activity-skeleton {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.empty-state {
  text-align: center;
  padding: var(--spacing-3xl) var(--spacing-lg);
}

.empty-icon {
  font-size: var(--font-size-5xl);
  color: var(--text-color-tertiary);
  margin-bottom: var(--spacing-lg);
}

.empty-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-color-secondary);
}

.empty-text {
  font-size: var(--font-size-lg);
  color: var(--text-color-tertiary);
  margin: 0 0 var(--spacing-xl) 0;
}

.create-activity-btn {
  font-size: var(--font-size-base);
  padding: var(--spacing-md) var(--spacing-xl);
  border-radius: var(--border-radius-lg);
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
}

.activity-card-item {
  transition: var(--transition-base);
}

.activity-card-item:hover {
  transform: translateY(-5px);
}

.load-more-section {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}

.load-more-btn {
  font-size: var(--font-size-base);
  padding: var(--spacing-md) var(--spacing-2xl);
  border-radius: var(--border-radius-full);
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xl);
  color: var(--text-color-secondary);
  font-size: var(--font-size-base);
}

/* 编辑对话框样式 */
.edit-form {
  padding: var(--spacing-md) 0;
}

.password-form {
  padding: var(--spacing-md) 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
}

/* 头像上传样式 */
.avatar-uploader {
  display: flex;
  justify-content: center;
}

.avatar-upload-content {
  width: 100px;
  height: 100px;
  border: 2px dashed var(--border-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.avatar-upload-content:hover {
  border-color: var(--color-primary);
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
}

.avatar-upload-icon {
  font-size: var(--font-size-2xl);
  color: var(--text-color-secondary);
}

.avatar-upload-text {
  font-size: var(--font-size-sm);
  color: var(--text-color-secondary);
  text-align: center;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .edit-buttons-corner {
    top: calc(var(--header-height) + var(--spacing-md));
    right: var(--spacing-md);
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .edit-btn,
  .password-btn {
    padding: var(--spacing-xs) var(--spacing-md);
    font-size: var(--font-size-xs);
  }
}

@media (max-width: 480px) {
  .edit-buttons-corner {
    top: calc(var(--header-height) + var(--spacing-sm));
    right: var(--spacing-sm);
  }

  .edit-btn,
  .password-btn {
    padding: var(--spacing-xs) var(--spacing-sm);
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .profile-info-card {
    flex-direction: column;
    text-align: center;
    gap: var(--spacing-2xl);
    padding: var(--spacing-2xl);
  }

  .divider-vertical {
    display: none;
  }

  .divider-horizontal {
    width: 60%;
    align-self: center;
  }

  .user-stats {
    justify-content: center;
  }

  .activities-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .tab-group {
    order: 2;
  }

  .action-buttons {
    order: 1;
    justify-content: center;
  }

  .avatar-section {
    min-width: auto;
  }
}

@media (max-width: 768px) {
  .profile-info-card {
    padding: var(--spacing-xl);
  }

  .username {
    font-size: var(--font-size-3xl);
  }

  .user-stats {
    gap: var(--spacing-lg);
    flex-wrap: wrap;
  }

  .stat-number {
    font-size: var(--font-size-3xl);
  }

  .activities-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }

  .section-container {
    padding: 0 var(--spacing-md);
  }

  .section-header .section-title {
    font-size: var(--font-size-3xl);
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-xs);
  }
}

@media (max-width: 480px) {
  .profile-content {
    padding: var(--spacing-lg);
  }

  .profile-info-card {
    padding: var(--spacing-lg);
  }

  .username {
    font-size: var(--font-size-2xl);
  }

  .user-description {
    font-size: var(--font-size-base);
  }

  .user-stats {
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .stat-item {
    padding: var(--spacing-lg);
  }

  .loading-grid,
  .activities-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-md);
  }
}
</style>
