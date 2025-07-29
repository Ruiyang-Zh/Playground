package cn.edu.nju.playground.controller;

import cn.edu.nju.playground.model.vo.ApiResponse;
import cn.edu.nju.playground.model.dto.user.*;
import cn.edu.nju.playground.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "用户服务")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public ApiResponse<UserInfoResponse> getCurrentUserInfo() {
        return ApiResponse.success(userService.getCurrentUserInfo());
    }

    @Operation(summary = "获取指定用户公开信息")
    @GetMapping("/{userId}/info")
    public ApiResponse<UserPublicInfoResponse> getUserPublicInfo(@PathVariable Long userId) {
        return ApiResponse.success(userService.getUserPublicInfo(userId));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public ApiResponse<UserInfoResponse> updateUserInfo(@Valid  @ModelAttribute UserUpdateRequest request) {
        return ApiResponse.success(userService.updateUserInfo(request));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return ApiResponse.success();
    }
}