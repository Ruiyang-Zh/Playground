package cn.edu.nju.playground.model.dto.activity;

import cn.edu.nju.playground.enums.ActivityStatus;
import cn.edu.nju.playground.enums.SportsType;
import cn.edu.nju.playground.model.dto.user.UserPublicInfoResponse;
import cn.edu.nju.playground.model.po.Activity;
import cn.edu.nju.playground.model.po.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityDetailResponse {
    private Long id;
    private String title;
    private SportsType type;
    private String description;
    private List<String> images;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String province;
    private String city;
    private String district;
    private String venue;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private BigDecimal fee;
    private LocalDateTime registrationDeadline;
    private String requirements;
    private ActivityStatus status;
    private UserPublicInfoResponse creator;
    private String contactInfo;
    private List<UserPublicInfoResponse> participants;
    private Boolean isRegistered;
    private Boolean canRegister;
    private Boolean canUpdate;
    private Boolean canCancel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ActivityDetailResponse(Activity activity, List<User> participants, boolean isRegistered, boolean canRegister, boolean canUpdate, boolean canCancel) {
        this.id = activity.getId();
        this.title = activity.getTitle();
        this.type = activity.getType();
        this.description = activity.getDescription();
        this.images = activity.getImages();
        this.startTime = activity.getStartTime();
        this.endTime = activity.getEndTime();
        this.province = activity.getProvince();
        this.city = activity.getCity();
        this.district = activity.getDistrict();
        this.venue = activity.getVenue();
        this.minParticipants = activity.getMinParticipants();
        this.maxParticipants = activity.getMaxParticipants();
        this.currentParticipants = activity.getCurrentParticipants();
        this.fee = activity.getFee();
        this.registrationDeadline = activity.getRegistrationDeadline();
        this.requirements = activity.getRequirements();
        this.status = activity.getStatus();
        this.creator = new UserPublicInfoResponse(activity.getCreator());
        this.contactInfo = activity.getContactInfo();
        this.participants = participants.stream()
                .map(UserPublicInfoResponse::new)
                .toList();
        this.isRegistered = isRegistered;
        this.canRegister = canRegister;
        this.canUpdate = canUpdate;
        this.canCancel = canCancel;
        this.createdAt = activity.getCreatedAt();
        this.updatedAt = activity.getUpdatedAt();
    }
}