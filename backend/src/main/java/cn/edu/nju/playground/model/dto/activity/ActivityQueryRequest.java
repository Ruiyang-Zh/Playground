package cn.edu.nju.playground.model.dto.activity;

import cn.edu.nju.playground.enums.ActivityStatus;
import cn.edu.nju.playground.enums.SportsType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityQueryRequest {
    private String keyword;
    private SportsType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minFee;
    private BigDecimal maxFee;
    private String province;
    private String city;
    private String district;
    private ActivityStatus status;
    private String sortBy = "startTime"; // startTime, fee, createdAt
    private String sortDir = "asc";      // asc, desc
}