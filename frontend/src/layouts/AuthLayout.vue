<template>
  <div class="auth-layout">
    <div class="auth-container">
      <!-- 左侧欢迎区域 -->
      <div class="welcome-section">
        <div class="welcome-content">
          <div class="brand-logo">
            <router-link to="/" class="brand-link">
              <h1 class="brand-text">PLAYGROUND</h1>
            </router-link>
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
import { Trophy, User, Location } from '@element-plus/icons-vue'

const route = useRoute()

const welcomeTitle = computed(() => {
  return route.name === 'Register' ? '加入运动社区' : '欢迎回到运动世界'
})

const welcomeSubtitle = computed(() => {
  return route.name === 'Register'
    ? '开启运动之旅，发现志同道合的运动爱好者'
    : '继续您的运动之旅，发现更多精彩活动和运动伙伴'
})

const features = [
  {
    icon: 'Trophy',
    text: '参与各种运动活动'
  },
  {
    icon: 'User',
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

/* 隐形链接样式和悬浮动态效果 */

.brand-link {
  /* 移除默认链接样式 */
  text-decoration: none;
  color: inherit;
  display: inline-block;

  /* 过渡动画 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  /* 确保可点击 */
  cursor: pointer;
}

.brand-link:hover,
.brand-link:focus {
  /* 移除任何默认的链接样式 */
  text-decoration: none;
  color: inherit;
  outline: none;

  /* 悬浮动态效果 */
  transform: translateY(-3px) scale(1.02);

  /* 发光效果 */
  filter: drop-shadow(0 8px 16px rgba(255, 255, 255, 0.15));
}

.brand-link:active {
  /* 点击时的效果 */
  transform: translateY(-1px) scale(1.01);
}

.brand-text {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-extrabold);
  letter-spacing: 2px;
  margin: 0;
  color: var(--text-color-inverse);

  /* 添加渐变效果 */
  background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;

  /* 过渡效果 */
  transition: all 0.3s ease;

  /* 防止文本选中 */
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
}

.brand-link:hover .brand-text {
  /* 悬浮时的文本效果 */
  background: linear-gradient(135deg, #ffffff 0%, #e0e0e0 50%, #ffffff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;

  /* 文字阴影效果 */
  text-shadow: 0 0 30px rgba(255, 255, 255, 0.3);

  /* 微妙的缩放 */
  transform: scale(1.01);
}

/* 为了确保在所有状态下都隐藏默认链接样式 */
.brand-link:link,
.brand-link:visited,
.brand-link:hover,
.brand-link:active {
  color: inherit;
  text-decoration: none;
}

/* 添加一个微妙的下划线动画（可选） */
.brand-link::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.brand-link:hover::after {
  width: 100%;
}

/* 为了让 ::after 伪元素正常工作，需要相对定位 */
.brand-logo {
  position: relative;
}

/* 响应式调整 */
@media (max-width: 1024px) {
  .brand-link:hover {
    transform: translateY(-2px) scale(1.01);
  }
}

@media (max-width: 768px) {
  .brand-link:hover {
    transform: translateY(-1px) scale(1.005);
  }

  .brand-link::after {
    display: none; /* 在小屏幕上隐藏下划线效果 */
  }
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
