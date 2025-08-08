package cn.edu.nju.playground.model.dto.user;

import cn.edu.nju.playground.enums.SportsType;
import cn.edu.nju.playground.model.po.User;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserPublicInfoResponse {
    private Long id;
    private String username;
    private String avatar;
    private List<SportsType> sportsPreference;
    private String description;

    public UserPublicInfoResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.avatar = user.getAvatar();
        this.sportsPreference = user.getSportsPreference();
        this.description = user.getDescription();
    }
}
