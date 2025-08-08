package cn.edu.nju.playground.controller;

import cn.edu.nju.playground.model.dto.comment.CommentCreateRequest;
import cn.edu.nju.playground.model.dto.comment.CommentResponse;
import cn.edu.nju.playground.model.vo.ApiResponse;
import cn.edu.nju.playground.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "活动评论")
@RestController
@RequestMapping("/api/activities/{activityId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "创建评论")
    @PostMapping
    public ApiResponse<CommentResponse> createComment(
            @PathVariable Long activityId,
            @Valid @RequestBody CommentCreateRequest request) {
        CommentResponse comment = commentService.createComment(activityId, request);
        return ApiResponse.success(comment);
    }

    @Operation(summary = "分页查询活动评论")
    @GetMapping
    public ApiResponse<Page<CommentResponse>> getActivityComments(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "true") boolean sortByNewest
            ) {
        Page<CommentResponse> comments = commentService.getActivityComments(activityId, page, size, sortByNewest);
        return ApiResponse.success(comments);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ApiResponse.success();
    }

    @Operation(summary = "获取活动评论总数")
    @GetMapping("/count")
    public ApiResponse<Long> getActivityCommentCount(@PathVariable Long activityId) {
        long count = commentService.getActivityCommentCount(activityId);
        return ApiResponse.success(count);
    }
}
