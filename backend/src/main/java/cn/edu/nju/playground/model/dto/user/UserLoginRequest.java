package cn.edu.nju.playground.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotBlank(message = "登录账号不能为空")
    private String account; // 手机号或邮箱

    @NotBlank(message = "密码不能为空")
    private String password;
}