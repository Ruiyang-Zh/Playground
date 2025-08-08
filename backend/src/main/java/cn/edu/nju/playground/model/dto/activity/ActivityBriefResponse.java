package cn.edu.nju.playground.model.dto.activity;

import cn.edu.nju.playground.enums.ActivityStatus;
import cn.edu.nju.playground.enums.SportsType;
import cn.edu.nju.playground.model.dto.user.UserPublicInfoResponse;
import cn.edu.nju.playground.model.po.Activity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityBriefResponse {
    private Long id;
    private String title;
    private SportsType type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String province;
    private String city;
    private String district;
    private String venue;
    private Integer currentParticipants;
    private Integer maxParticipants;
    private BigDecimal fee;
    private ActivityStatus status;
    private LocalDateTime registrationDeadline;
    private UserPublicInfoResponse creator;
    private Boolean isRegistered;
    private String image;
    private LocalDateTime createdAt;

    public ActivityBriefResponse(Activity activity, boolean isRegistered) {
        this.id = activity.getId();
        this.title = activity.getTitle();
        this.type = activity.getType();
        this.startTime = activity.getStartTime();
        this.endTime = activity.getEndTime();
        this.province = activity.getProvince();
        this.city = activity.getCity();
        this.district = activity.getDistrict();
        this.venue = activity.getVenue();
        this.currentParticipants = activity.getCurrentParticipants();
        this.maxParticipants = activity.getMaxParticipants();
        this.fee = activity.getFee();
        this.status = activity.getStatus();
        this.registrationDeadline = activity.getRegistrationDeadline();
        this.creator = new UserPublicInfoResponse(activity.getCreator());
        this.isRegistered = isRegistered;
        this.image = activity.getImages().isEmpty() ? null : activity.getImages().get(0);
        this.createdAt = activity.getCreatedAt();
    }
}
