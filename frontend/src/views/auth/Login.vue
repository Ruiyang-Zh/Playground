<template>
  <div class="login-card">
    <div class="card-header">
      <h1 class="card-title">登录账户</h1>
      <p class="card-subtitle">使用您的邮箱或手机号登录</p>
    </div>

    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      class="login-form"
      @submit.prevent="handleLogin"
    >
      <el-form-item prop="account">
        <el-input
          v-model="loginForm.account"
          placeholder="邮箱或手机号"
          size="large"
          class="form-input"
        >
          <template #prefix>
            <el-icon class="input-icon"><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="密码"
          size="large"
          show-password
          class="form-input"
          @keyup.enter="handleLogin"
        >
          <template #prefix>
            <el-icon class="input-icon"><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <div class="form-options">
        <el-checkbox v-model="rememberMe" class="remember-checkbox">
          记住我
        </el-checkbox>
        <el-button type="text" class="forgot-link">
          忘记密码？
        </el-button>
      </div>

      <el-button
        type="primary"
        size="large"
        class="submit-btn"
        :loading="loading"
        @click="handleLogin"
      >
        {{ loading ? '登录中...' : '登录' }}
      </el-button>
    </el-form>

    <div class="card-footer">
      <p class="switch-prompt">
        还没有账户？
        <router-link to="/auth/register" class="switch-link">
          立即注册
        </router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useAuth } from '@/composables/useAuth'
import type { UserLoginRequest } from '@/types/user'

const router = useRouter()
const route = useRoute()
const { login } = useAuth()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive<UserLoginRequest>({
  account: '',
  password: ''
})

const loginRules = {
  account: [
    { required: true, message: '请输入邮箱或手机号', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        const phoneRegex = /^1[3-9]\d{9}$/
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

        if (!phoneRegex.test(value) && !emailRegex.test(value)) {
          callback(new Error('请输入正确的邮箱或手机号格式'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    loading.value = true

    await login(loginForm)

    ElMessage.success('登录成功！')

    const redirect = route.query.redirect as string
    router.push(redirect || '/')

  } catch (error: any) {
    console.error('Login error:', error)
    ElMessage.error(error.message || '登录失败，请检查账号和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-card {
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-lg);
  padding: var(--spacing-2xl);
  width: 100%;
}

.card-header {
  text-align: center;
  margin-bottom: var(--spacing-2xl);
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

.login-form {
  margin-bottom: var(--spacing-xl);
}

.login-form :deep(.el-form-item) {
  margin-bottom: var(--spacing-lg);
}

.form-input :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-lg);
  height: 50px;
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

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.remember-checkbox {
  font-size: var(--font-size-sm);
}

.forgot-link {
  font-size: var(--font-size-sm);
  color: var(--primary-color);
  padding: 0;
}

.forgot-link:hover {
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
</style>
