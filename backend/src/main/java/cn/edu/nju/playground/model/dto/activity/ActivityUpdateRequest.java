package cn.edu.nju.playground.model.dto.activity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityUpdateRequest {
    @Length(max = 200, message = "活动标题长度不能超过200字符")
    private String title;
    private String description;
    private List<MultipartFile> images;
    @Future(message = "开始时间必须在当前时间之后")
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String province;
    private String city;
    private String district;
    private String venue;
    @Min(value = 1, message = "最少参与人数不能小于1")
    private Integer minParticipants;
    @Min(value = 1, message = "最多参与人数不能小于1")
    private Integer maxParticipants;
    private LocalDateTime registrationDeadline;
    @Length(max = 200, message = "联系方式长度不能超过200字符")
    private String contactInfo;
    private String requirements;
}
