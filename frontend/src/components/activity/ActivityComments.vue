<template>
  <div class="activity-comments">
    <div class="comments-header">
      <h3 class="comments-title">
        <el-icon><ChatLineRound /></el-icon>
        讨论区 ({{ pagination.total }})
      </h3>
    </div>

    <!-- 发表评论 -->
    <div v-if="isLoggedIn" class="comment-input-section">
      <div class="input-header">
        <UserAvatar
          :user="currentUser"
          size="medium"
          :clickable="false"
          :show-hover="false"
          class="input-avatar"
        />
        <div class="input-info">
          <span class="input-username">{{ currentUser?.username }}</span>
          <span class="input-label">发表你的看法...</span>
        </div>
      </div>

      <div class="input-container">
        <el-input
          v-model="newComment"
          type="textarea"
          :rows="3"
          placeholder="说点什么吧..."
          maxlength="500"
          show-word-limit
          class="comment-input"
          @focus="isInputFocused = true"
          @blur="handleInputBlur"
        />

        <div v-if="isInputFocused || newComment.trim()" class="input-actions">
          <el-button size="small" @click="cancelComment">取消</el-button>
          <el-button
            type="primary"
            size="small"
            @click="submitComment"
            :loading="submitting"
            :disabled="!newComment.trim()"
          >
            发表评论
          </el-button>
        </div>
      </div>
    </div>

    <!-- 未登录提示 -->
    <div v-else class="login-prompt">
      <el-icon class="login-icon"><User /></el-icon>
      <span class="login-text">
        <router-link to="/auth/login" class="login-link">登录</router-link>
        后参与讨论
      </span>
    </div>

    <!-- 评论列表 -->
    <div class="comments-list">
      <div v-if="loading && comments.length === 0" class="loading-comments">
        <el-skeleton animated>
          <template #template>
            <div v-for="i in 3" :key="i" class="comment-skeleton">
              <el-skeleton-item variant="circle" style="width: 40px; height: 40px;" />
              <div class="skeleton-content">
                <el-skeleton-item variant="text" style="width: 120px; height: 16px;" />
                <el-skeleton-item variant="text" style="width: 100%; height: 60px; margin-top: 8px;" />
                <el-skeleton-item variant="text" style="width: 200px; height: 14px; margin-top: 8px;" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>

      <div v-else-if="comments.length === 0" class="no-comments">
        <el-icon class="no-comments-icon"><ChatLineRound /></el-icon>
        <p class="no-comments-text">还没有人参与活动讨论，开启一个话题一起聊聊吧！</p>
      </div>

      <div v-else class="comments-container">
        <div
          v-for="comment in comments"
          :key="comment.id"
          class="comment-item"
        >
          <!-- 用户头像 -->
          <UserAvatar
            :user="comment.user"
            size="medium"
            class="comment-avatar"
          />

          <!-- 评论内容 -->
          <div class="comment-content">
            <!-- 用户信息和时间 -->
            <div class="comment-header">
              <div class="author-info">
                <span class="author-name">{{ comment.user.username }}</span>
              </div>
              <div class="comment-meta">
                <span class="comment-time">{{ formatRelativeTime(comment.createdAt) }}</span>
              </div>
            </div>

            <!-- 评论正文 -->
            <div class="comment-body">
              <div class="comment-text">
                {{ comment.content }}
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="comment-actions">
              <el-dropdown
                v-if="canDeleteComment(comment)"
                trigger="click"
                placement="bottom-end"
                class="more-actions"
              >
                <button class="action-btn more-btn">
                  <el-icon><MoreFilled /></el-icon>
                </button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="confirmDelete(comment.id)" class="delete-item">
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div v-if="pagination.hasMore" class="load-more">
          <el-button @click="loadMore" :loading="loading" class="load-more-btn">
            加载更多评论
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatLineRound, User, MoreFilled, Delete } from '@element-plus/icons-vue'
import { useAuth } from '@/composables/useAuth'
import { useComment } from '@/composables/useComment'
import UserAvatar from '@/components/common/UserAvatar.vue'
import { formatRelativeTime } from '@/utils/format'

interface Props {
  activityId: number
}

const props = defineProps<Props>()

const { isLoggedIn, currentUser } = useAuth()
const {
  comments,
  loading,
  submitting,
  pagination,
  fetchComments,
  createComment,
  deleteComment,
  loadMore,
  canDeleteComment
} = useComment(props.activityId)

// 新评论输入
const newComment = ref('')
const isInputFocused = ref(false)

// 方法
const submitComment = async () => {
  if (!newComment.value.trim()) return

  try {
    await createComment({
      content: newComment.value.trim()
    })

    newComment.value = ''
    isInputFocused.value = false
  } catch (error) {
    console.error('Failed to submit comment:', error)
  }
}

const cancelComment = () => {
  newComment.value = ''
  isInputFocused.value = false
}

const handleInputBlur = () => {
  if (!newComment.value.trim()) {
    setTimeout(() => {
      isInputFocused.value = false
    }, 100)
  }
}

const confirmDelete = async (commentId: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条评论吗？删除后无法恢复。',
      '确认删除',
      {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    await deleteComment(commentId)
  } catch (error) {
    // 用户取消删除或删除失败
    if (error !== 'cancel') {
      console.error('Failed to delete comment:', error)
    }
  }
}

// 组件挂载时加载评论
onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.activity-comments {
  width: 100%;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color-light);
}

.comments-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

/* 评论输入区域 */
.comment-input-section {
  margin-bottom: var(--spacing-xl);
  background: var(--bg-color-secondary);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
}

.input-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.input-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.input-username {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
}

.input-label {
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
}

.input-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.comment-input :deep(.el-textarea__inner) {
  border-radius: var(--border-radius-md);
  border: 2px solid var(--border-color);
  transition: var(--transition-base);
}

.comment-input :deep(.el-textarea__inner):focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
}

/* 未登录提示 */
.login-prompt {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xl);
  background: var(--bg-color-secondary);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-xl);
  color: var(--text-color-secondary);
}

.login-icon {
  font-size: var(--font-size-lg);
  color: var(--text-color-tertiary);
}

.login-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
}

.login-link:hover {
  text-decoration: underline;
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.loading-comments {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.comment-skeleton {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
}

.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.no-comments {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl);
  color: var(--text-color-tertiary);
  text-align: center;
}

.no-comments-icon {
  font-size: var(--font-size-4xl);
  margin-bottom: var(--spacing-lg);
}

.no-comments-text {
  font-size: var(--font-size-base);
  margin: 0;
}

.comments-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

/* 评论项 */
.comment-item {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--bg-color-primary);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-color-light);
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-sm);
}

.author-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.author-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-primary);
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-sm);
  color: var(--text-color-tertiary);
}

.comment-body {
  margin-bottom: var(--spacing-md);
}

.comment-text {
  font-size: var(--font-size-base);
  color: var(--text-color-primary);
  line-height: 1.6;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.comment-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  background: none;
  border: none;
  color: var(--text-color-tertiary);
  font-size: var(--font-size-sm);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: var(--transition-base);
}

.action-btn:hover {
  color: var(--text-color-secondary);
  background: var(--bg-color-secondary);
}

.more-actions :deep(.el-dropdown__caret-button) {
  display: none;
}

.load-more {
  display: flex;
  justify-content: center;
  padding: var(--spacing-xl) 0;
}

.load-more-btn {
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-md) var(--spacing-xl);
}

:deep(.el-dropdown-menu__item.delete-item) {
  color: var(--danger-color);
}

:deep(.el-dropdown-menu__item.delete-item:hover) {
  background: rgba(245, 108, 108, 0.1);
  color: var(--danger-color);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .comments-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }

  .comment-input-section {
    padding: var(--spacing-md);
  }

  .input-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }

  .comment-item {
    padding: var(--spacing-md);
  }

  .comment-header {
    flex-direction: column;
    gap: var(--spacing-xs);
    align-items: flex-start;
  }
}
</style>
