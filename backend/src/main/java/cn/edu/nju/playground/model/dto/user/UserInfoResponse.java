package cn.edu.nju.playground.model.dto.user;

import cn.edu.nju.playground.enums.SportsType;
import cn.edu.nju.playground.model.po.User;
import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponse {
    private Long id;
    private String phone;
    private String email;
    private String username;
    private String avatar;
    private List<SportsType> sportsPreference;
    private String description;
    private String createdAt;

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.avatar = user.getAvatar();
        this.sportsPreference = user.getSportsPreference();
        this.description = user.getDescription();
        this.createdAt = user.getCreatedAt().toString();
    }
}