package cn.edu.nju.playground.model.dto.activity;

import cn.edu.nju.playground.enums.ActivityStatus;
import cn.edu.nju.playground.enums.SportsType;
import cn.edu.nju.playground.enums.UserActivityType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserActivityQueryRequest {

    // 用户参与类型
    private UserActivityType participationType; // CREATED,JOINED,APPLIED,CANCELLED

    // 活动属性筛选
    private SportsType activityType;
    private ActivityStatus activityStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String province;
    private String city;

    // 排序条件
    private String sortBy = "time"; // time,activityTime,fee
    private String sortDir = "desc"; // asc,desc
}