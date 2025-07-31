import { ref, computed, readonly } from 'vue'
import * as commentAPI from '@/api/comment'
import { useAuth } from './useAuth'
import { ElMessage } from 'element-plus'
import type { CommentResponse, CommentCreateRequest } from '@/types/comment'

export function useComment(activityId: number) {
  const { requireAuth, currentUser } = useAuth()

  const comments = ref<CommentResponse[]>([])
  const loading = ref(false)
  const submitting = ref(false)
  const pagination = ref({
    page: 0,
    size: 20,
    total: 0,
    hasMore: true
  })

  const fetchComments = async (page = 0, append = false) => {
    try {
      loading.value = true
      const response = await commentAPI.getActivityComments(activityId, {
        page,
        size: pagination.value.size,
        sortByNewest: true
      })

      if (append) {
        comments.value.push(...response.data.content)
      } else {
        comments.value = response.data.content
      }

      pagination.value = {
        page: response.data.number,
        size: response.data.size,
        total: response.data.totalElements,
        hasMore: !response.data.last
      }

      return response.data
    } catch (error) {
      console.error('Failed to fetch comments:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createComment = async (data: CommentCreateRequest) => {
    return requireAuth(async () => {
      try {
        submitting.value = true
        const response = await commentAPI.createComment(activityId, data)

        // 将新评论添加到列表顶部
        comments.value.unshift(response.data)
        pagination.value.total += 1

        ElMessage.success(data.parentId ? '回复成功！' : '评论成功！')
        return response.data
      } catch (error) {
        ElMessage.error('发布失败，请重试')
        throw error
      } finally {
        submitting.value = false
      }
    })
  }

  const deleteComment = async (commentId: number) => {
    return requireAuth(async () => {
      try {
        await commentAPI.deleteComment(activityId, commentId)

        // 从列表中移除评论
        const index = comments.value.findIndex(c => c.id === commentId)
        if (index > -1) {
          comments.value.splice(index, 1)
          pagination.value.total -= 1
        }

        ElMessage.success('删除成功')
      } catch (error) {
        ElMessage.error('删除失败，请重试')
        throw error
      }
    })
  }

  const loadMore = async () => {
    if (pagination.value.hasMore && !loading.value) {
      await fetchComments(pagination.value.page + 1, true)
    }
  }

  const canDeleteComment = (comment: CommentResponse) => {
    return currentUser.value?.id === comment.user.id
  }

  return {
    comments: readonly(comments),
    loading: readonly(loading),
    submitting: readonly(submitting),
    pagination: readonly(pagination),
    fetchComments,
    createComment,
    deleteComment,
    loadMore,
    canDeleteComment
  }
}
