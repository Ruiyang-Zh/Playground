package cn.edu.nju.playground.event.activity;

import cn.edu.nju.playground.model.po.Activity;
import cn.edu.nju.playground.model.po.User;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ActivityFeeRefundEvent {
    private User user;
    private BigDecimal amount;
    private Activity activity;
    private String description;
}
