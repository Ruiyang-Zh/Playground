package cn.edu.nju.playground.controller;

import cn.edu.nju.playground.model.dto.activity.*;
import cn.edu.nju.playground.model.vo.ApiResponse;
import cn.edu.nju.playground.service.ActivityService;
import cn.edu.nju.playground.util.TokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "活动管理")
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final TokenUtil tokenUtil;

    @Operation(summary = "创建活动")
    @PostMapping
    public ApiResponse<Long> createActivity(@Valid @ModelAttribute ActivityCreateRequest request) {
        Long activityId = activityService.createActivity(request);
        return ApiResponse.success(activityId);
    }

    @Operation(summary = "获取活动列表")
    @GetMapping
    public ApiResponse<Page<ActivityBriefResponse>> getActivities(
            @Valid ActivityQueryRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ActivityBriefResponse> activities = activityService.getActivities(request, page, size);
        return ApiResponse.success(activities);
    }

    @Operation(summary = "查询用户活动")
    @GetMapping("/users/{userId}")
    public ApiResponse<Page<ActivityBriefResponse>> getUserActivities(
            @PathVariable Long userId,
            @Valid UserActivityQueryRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ActivityBriefResponse> activities = activityService.getUserActivities(userId, request, page, size);
        return ApiResponse.success(activities);
    }

    @Operation(summary = "查询当前用户活动")
    @GetMapping("/users/my")
    public ApiResponse<Page<ActivityBriefResponse>> getCurrentUserActivities(
            @Valid UserActivityQueryRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ActivityBriefResponse> activities = activityService.getUserActivities(tokenUtil.getCurrentUserId(), request, page, size);
        return ApiResponse.success(activities);
    }

    @Operation(summary = "获取活动详情")
    @GetMapping("/{activityId}")
    public ApiResponse<ActivityDetailResponse> getActivityDetail(@PathVariable Long activityId) {
        ActivityDetailResponse activity = activityService.getActivityDetail(activityId);
        return ApiResponse.success(activity);
    }

    @Operation(summary = "更新活动")
    @PutMapping("/{activityId}")
    public ApiResponse<Void> updateActivity(
            @PathVariable Long activityId,
            @Valid @ModelAttribute ActivityUpdateRequest request) {
        activityService.updateActivity(activityId, request);
        return ApiResponse.success();
    }

    @Operation(summary = "取消活动")
    @DeleteMapping("/{activityId}")
    public ApiResponse<Void> cancelActivity(@PathVariable Long activityId) {
        activityService.cancelActivity(activityId);
        return ApiResponse.success();
    }

    @Operation(summary = "报名活动")
    @PostMapping("/{activityId}/register")
    public ApiResponse<Void> registerActivity(@PathVariable Long activityId) {
        activityService.registerActivity(activityId);
        return ApiResponse.success();
    }

    @Operation(summary = "退出活动")
    @DeleteMapping("/{activityId}/register")
    public ApiResponse<Void> unregisterActivity(@PathVariable Long activityId) {
        activityService.unregisterActivity(activityId);
        return ApiResponse.success();
    }
}