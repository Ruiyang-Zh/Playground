<template>
  <div class="auth-layout">
    <div class="auth-container">
      <!-- 左侧欢迎区域 -->
      <div class="welcome-section">
        <div class="welcome-content">
          <div class="brand-logo">
            <h1 class="brand-text">PLAYGROUND</h1>
          </div>

          <div class="welcome-text">
            <h2 class="welcome-title">{{ welcomeTitle }}</h2>
            <p class="welcome-subtitle">{{ welcomeSubtitle }}</p>
          </div>

          <div class="welcome-features">
            <div class="feature-item" v-for="feature in features" :key="feature.text">
              <el-icon class="feature-icon">
                <component :is="feature.icon" />
              </el-icon>
              <span class="feature-text">{{ feature.text }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="form-section">
        <div class="form-container">
          <router-view />
        </div>
      </div>
    </div>

    <!-- 背景 -->
    <div class="auth-background">
      <div class="bg-gradient"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Trophy, Users, Location } from '@element-plus/icons-vue'

const route = useRoute()

const welcomeTitle = computed(() => {
  return route.name === 'Register' ? '加入运动社区' : '欢迎回到运动世界'
})

const welcomeSubtitle = computed(() => {
  return route.name === 'Register'
    ? '与志同道合的运动爱好者一起，开启健康活力的生活方式'
    : '继续您的运动之旅，发现更多精彩活动和运动伙伴'
})

const features = [
  {
    icon: 'Trophy',
    text: '参与各种运动活动'
  },
  {
    icon: 'Users',
    text: '结识运动伙伴'
  },
  {
    icon: 'Location',
    text: '发现周边场地'
  }
]
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.auth-container {
  position: relative;
  z-index: 10;
  min-height: 100vh;
  display: flex;
}

.welcome-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl);
  color: var(--text-color-inverse);
}

.welcome-content {
  max-width: 500px;
  width: 100%;
}

.brand-logo {
  margin-bottom: var(--spacing-2xl);
}

.brand-text {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-extrabold);
  letter-spacing: 2px;
  margin: 0;
  color: var(--text-color-inverse);
}

.welcome-text {
  margin-bottom: var(--spacing-3xl);
}

.welcome-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-lg) 0;
  line-height: 1.3;
}

.welcome-subtitle {
  font-size: var(--font-size-lg);
  line-height: 1.6;
  margin: 0;
  opacity: 0.9;
}

.welcome-features {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

.feature-icon {
  font-size: var(--font-size-xl);
  opacity: 0.8;
}

.feature-text {
  opacity: 0.9;
}

.form-section {
  flex: 0 0 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.form-container {
  width: 100%;
  max-width: 400px;
}

.auth-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--gradient-user-bg);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .auth-container {
    flex-direction: column;
  }

  .welcome-section {
    flex: 0 0 40vh;
    padding: var(--spacing-xl);
  }

  .welcome-content {
    max-width: 100%;
    text-align: center;
  }

  .brand-text {
    font-size: var(--font-size-3xl);
  }

  .welcome-title {
    font-size: var(--font-size-2xl);
  }

  .welcome-features {
    flex-direction: row;
    justify-content: center;
    flex-wrap: wrap;
  }

  .form-section {
    flex: 1;
    background: var(--bg-color-primary);
    backdrop-filter: none;
  }
}

@media (max-width: 768px) {
  .welcome-section {
    padding: var(--spacing-lg);
  }

  .form-section {
    padding: var(--spacing-lg);
  }

  .welcome-features {
    gap: var(--spacing-md);
  }

  .feature-item {
    flex-direction: column;
    text-align: center;
    gap: var(--spacing-xs);
  }
}
</style>
