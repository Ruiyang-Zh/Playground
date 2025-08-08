package cn.edu.nju.playground.model.dto.user;

import cn.edu.nju.playground.enums.SportsType;
import lombok.Data;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UserRegisterRequest {
    @NotBlank
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20位之间")
    private String username;

    private MultipartFile avatar;
    private List<SportsType> sportsPreference;
    private String description;
}