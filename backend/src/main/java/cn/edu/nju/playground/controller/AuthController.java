package cn.edu.nju.playground.controller;

import cn.edu.nju.playground.model.dto.user.UserLoginRequest;
import cn.edu.nju.playground.model.dto.user.UserRegisterRequest;
import cn.edu.nju.playground.model.vo.ApiResponse;
import cn.edu.nju.playground.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "注册登录服务")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid  @ModelAttribute UserRegisterRequest request) {
        userService.register(request);
        return ApiResponse.success();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody UserLoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }
}
