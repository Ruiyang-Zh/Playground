package cn.edu.nju.playground.model.dto.activity;

import cn.edu.nju.playground.enums.SportsType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Valid
public class ActivityCreateRequest {
    @NotBlank(message = "活动标题不能为空")
    @Length(max = 200, message = "活动标题长度不能超过200字符")
    private String title;

    @NotNull(message = "活动类型不能为空")
    private SportsType type;

    private String description;

    private List<MultipartFile> images;

    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须在当前时间之后")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "具体场馆地址不能为空")
    private String venue;

    @NotNull(message = "最少参与人数不能为空")
    @Min(value = 1, message = "最少参与人数不能小于1")
    private Integer minParticipants;

    @NotNull(message = "最多参与人数不能为空")
    @Min(value = 1, message = "最多参与人数不能小于1")
    private Integer maxParticipants;

    @NotNull(message = "参与费用不能为空")
    @DecimalMin(value = "0.00", message = "参与费用不能为负数")
    private BigDecimal fee;

    @NotNull(message = "报名截止时间不能为空")
    private LocalDateTime registrationDeadline;

    @NotBlank(message = "联系方式不能为空")
    @Length(max = 100, message = "联系方式长度不能超过100字符")
    private String contactInfo;

    private String requirements;
}
