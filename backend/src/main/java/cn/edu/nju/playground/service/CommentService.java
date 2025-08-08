package cn.edu.nju.playground.service;

import cn.edu.nju.playground.enums.ActivityStatus;
import cn.edu.nju.playground.exception.PlaygroundException;
import cn.edu.nju.playground.model.dto.comment.CommentCreateRequest;
import cn.edu.nju.playground.model.dto.comment.CommentResponse;
import cn.edu.nju.playground.model.po.Activity;
import cn.edu.nju.playground.model.po.Comment;
import cn.edu.nju.playground.model.po.User;
import cn.edu.nju.playground.repository.ActivityRepository;
import cn.edu.nju.playground.repository.CommentRepository;
import cn.edu.nju.playground.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ActivityRepository activityRepository;
    private final TokenUtil tokenUtil;

    /**
     * 创建评论
     */
    @Transactional
    public CommentResponse createComment(Long activityId, CommentCreateRequest request) {
        User currentUser = tokenUtil.getCurrentUser();
        if (currentUser == null) {
            throw PlaygroundException.notLoggedIn();
        }

        // 验证活动是否存在
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> PlaygroundException.badRequest("活动不存在"));

        // 验证活动状态
        if (activity.getStatus() == ActivityStatus.CANCELLED) {
            throw PlaygroundException.badRequest("活动已取消，无法评论");
        }

        // 验证父评论是否存在（如果指定了父评论）
        Comment parentComment = null;
        if (request.getParentId() != null) {
            parentComment = commentRepository.findByIdAndPresent(request.getParentId())
                    .orElseThrow(() -> PlaygroundException.badRequest("父评论不存在"));

            // 验证父评论是否属于同一个活动
            if (!parentComment.getActivity().getId().equals(activityId)) {
                throw PlaygroundException.badRequest("父评论与活动不匹配");
            }
        }

        // 创建评论
        Comment comment = Comment.builder()
                .activity(activity)
                .user(currentUser)
                .parent(parentComment)
                .content(request.getContent().trim())
                .build();

        comment = commentRepository.save(comment);

        log.debug("用户创建评论成功，评论ID: {}, 活动ID: {}, 用户ID: {}",
                comment.getId(), activityId, currentUser.getId());

        return new CommentResponse(comment);
    }

    /**
     * 分页查询活动评论
     */
    public Page<CommentResponse> getActivityComments(Long activityId, int page, int size, boolean sortByNewest) {
        // 验证活动是否存在
        if (!activityRepository.existsById(activityId)) {
            throw PlaygroundException.badRequest("活动不存在");
        }

        Sort sort = sortByNewest ? Sort.by("createdAt").descending() : Sort.by("createdAt").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Comment> comments = commentRepository.findByActivity_Id(activityId, pageable);

        return comments.map(CommentResponse::new);
    }

    /**
     * 删除评论
     */
    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = tokenUtil.getCurrentUser();
        if (currentUser == null) {
            throw PlaygroundException.notLoggedIn();
        }

        Comment comment = commentRepository.findByIdAndPresent(commentId)
                .orElseThrow(() -> PlaygroundException.badRequest("评论不存在"));

        // 检查权限：只有评论作者可以删除自己的评论
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw PlaygroundException.operationNotAllowed("只能删除自己的评论");
        }

        // 删除评论内容
        comment.setContent(null);

        commentRepository.save(comment);

        log.debug("用户删除评论成功，评论ID: {}, 用户ID: {}", commentId, currentUser.getId());
    }

    /**
     * 获取活动评论总数
     */
    public long getActivityCommentCount(Long activityId) {
        return commentRepository.countByActivity_Id(activityId);
    }
}
