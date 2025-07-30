package cn.edu.nju.playground.event.activity;

import cn.edu.nju.playground.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityEventListener {

    // 这里需要注入钱包服务，暂时用注释标注
    // private final WalletService walletService;

    private final ActivityService activityService;

    @EventListener
    public void handleFeeChargeEvent(ActivityFeeChargeEvent event) {
        log.info("处理活动费用扣除事件: 用户ID={}, 金额={}, 活动ID={}, 描述={}",
                event.getUser().getId(), event.getAmount(), event.getActivity().getId(), event.getDescription());

        // TODO: 调用钱包服务扣除费用
        // walletService.deduct(event.getUserId(), event.getAmount(), TransactionType.ACTIVITY_FEE, event.getDescription(), event.getActivityId());

        // 模拟扣费成功
        activityService.confirmRegistration(event.getActivity().getId());
    }

    @EventListener
    public void handleFeeRefundEvent(ActivityFeeRefundEvent event) {
        log.info("处理活动费用退还事件: 用户ID={}, 金额={}, 活动ID={}, 描述={}",
                event.getUser().getId(), event.getAmount(), event.getActivity().getId(), event.getDescription());

        // TODO: 调用钱包服务退还费用
        // walletService.refund(event.getUserId(), event.getAmount(), TransactionType.ACTIVITY_REFUND, event.getDescription(), event.getActivityId());
    }
}