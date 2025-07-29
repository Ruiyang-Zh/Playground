package cn.edu.nju.playground.model.dto.user;

import cn.edu.nju.playground.enums.SportsType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
public class UserUpdateRequest {
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(min = 2, max = 20, message = "用户名长度必须在2-20位之间")
    private String username;

    private MultipartFile avatar;
    private String contactInfo;
    private List<SportsType> sportsPreference;
    private String description;
}