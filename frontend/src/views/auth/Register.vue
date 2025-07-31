<template>
  <div class="register-card">
    <div class="card-header">
      <h1 class="card-title">创建账户</h1>
      <p class="card-subtitle">填写信息开始您的运动之旅</p>
    </div>

    <el-form
      ref="registerFormRef"
      :model="registerForm"
      :rules="registerRules"
      class="register-form"
      @submit.prevent="handleRegister"
    >
      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          placeholder="用户名"
          size="large"
          class="form-input"
        >
          <template #prefix>
            <el-icon class="input-icon"><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item prop="email">
        <el-input
          v-model="registerForm.email"
          placeholder="邮箱地址"
          size="large"
          class="form-input"
        >
          <template #prefix>
            <el-icon class="input-icon"><Message /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item prop="phone">
        <el-input
          v-model="registerForm.phone"
          placeholder="手机号码"
          size="large"
          class="form-input"
        >
          <template #prefix>
            <el-icon class="input-icon"><Iphone /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          placeholder="设置密码"
          size="large"
          show-password
          class="form-input"
        >
          <template #prefix>
            <el-icon class="input-icon"><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          placeholder="确认密码"
          size="large"
          show-password
          class="form-input"
          @keyup.enter="handleRegister"
        >
          <template #prefix>
            <el-icon class="input-icon"><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <!-- 运动偏好 -->
      <div class="sports-section">
        <label class="section-label">选择感兴趣的运动</label>
        <div class="sports-grid">
          <el-checkbox
            v-for="sport in SPORTS_OPTIONS.slice(0, 6)"
            :key="sport.value"
            :value="sport.value"
            v-model="registerForm.sportsPreference"
            class="sport-item"
          >
            {{ sport.label }}
          </el-checkbox>
        </div>
      </div>

      <!-- 服务条款 -->
      <el-form-item prop="agreeTerms" class="terms-item">
        <el-checkbox v-model="registerForm.agreeTerms" class="terms-checkbox">
          我同意
          <el-button type="text" class="terms-link">服务条款</el-button>
          和
          <el-button type="text" class="terms-link">隐私政策</el-button>
        </el-checkbox>
      </el-form-item>

      <el-button
        type="primary"
        size="large"
        class="submit-btn"
        :loading="loading"
        @click="handleRegister"
      >
        {{ loading ? '注册中...' : '立即注册' }}
      </el-button>
    </el-form>

    <div class="card-footer">
      <p class="switch-prompt">
        已有账户？
        <router-link to="/auth/login" class="switch-link">
          立即登录
        </router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { User, Message, Iphone, Lock } from '@element-plus/icons-vue'
import { SPORTS_OPTIONS } from '@/utils/constants'
import * as authAPI from '@/api/auth'
import type { SportsType } from '@/types/user'

const router = useRouter()

const registerFormRef = ref<FormInstance>()
const loading = ref(false)

interface RegisterForm {
  username: string
  phone: string
  email: string
  password: string
  confirmPassword: string
  sportsPreference: SportsType[]
  agreeTerms: boolean
}

const registerForm = reactive<RegisterForm>({
  username: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  sportsPreference: [],
  agreeTerms: false
})

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  agreeTerms: [
    {
      validator: (rule: any, value: boolean, callback: Function) => {
        if (!value) {
          callback(new Error('请同意服务条款和隐私政策'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    loading.value = true

    const formData = new FormData()
    formData.append('username', registerForm.username)
    formData.append('phone', registerForm.phone)
    formData.append('email', registerForm.email)
    formData.append('password', registerForm.password)

    registerForm.sportsPreference.forEach(sport => {
      formData.append('sportsPreference', sport)
    })

    await authAPI.register(formData)

    ElMessage.success('注册成功！请登录您的账户')
    router.push('/auth/login')

  } catch (error: any) {
    console.error('Register error:', error)
    ElMessage.error(error.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-card {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-lg);
  padding: var(--spacing-2xl);
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.card-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.card-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-sm) 0;
  color: var(--text-color-primary);
}

.card-subtitle {
  font-size: var(--font-size-base);
  color: var(--text-color-secondary);
  margin: 0;
}

.register-form {
  margin-bottom: var(--spacing-lg);
}

.register-form :deep(.el-form-item) {
  margin-bottom: var(--spacing-md);
}

.form-input :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-lg);
  height: 48px;
  border: 2px solid var(--border-color);
  transition: var(--transition-base);
}

.form-input :deep(.el-input__wrapper):hover {
  border-color: var(--primary-color);
}

.form-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.form-input :deep(.el-input__inner) {
  font-size: var(--font-size-base);
  padding: 0 var(--spacing-lg);
}

.input-icon {
  color: var(--text-color-secondary);
  margin-left: var(--spacing-sm);
}

.sports-section {
  margin: var(--spacing-xl) 0;
}

.section-label {
  display: block;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-color-primary);
  margin-bottom: var(--spacing-md);
}

.sports-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-sm);
}

.sport-item {
  font-size: var(--font-size-sm);
}

.terms-item {
  margin-bottom: var(--spacing-xl);
}

.terms-checkbox {
  font-size: var(--font-size-sm);
}

.terms-link {
  color: var(--primary-color);
  padding: 0;
  font-size: var(--font-size-sm);
}

.terms-link:hover {
  text-decoration: underline;
}

.submit-btn {
  width: 100%;
  height: 50px;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  border-radius: var(--border-radius-lg);
  transition: var(--transition-base);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.card-footer {
  text-align: center;
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color-light);
}

.switch-prompt {
  font-size: var(--font-size-sm);
  color: var(--text-color-secondary);
  margin: 0;
}

.switch-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
}

.switch-link:hover {
  text-decoration: underline;
}

/* 滚动条样式 */
.register-card::-webkit-scrollbar {
  width: 4px;
}

.register-card::-webkit-scrollbar-track {
  background: var(--bg-color-secondary);
  border-radius: 2px;
}

.register-card::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 2px;
}
</style>
