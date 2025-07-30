package cn.edu.nju.playground.event.activity;

import cn.edu.nju.playground.service.ActivityService;
import cn.edu.nju.playground.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityEventListener {

    private final ActivityService activityService;
    private final WalletService walletService;

    @EventListener
    public void handleRegistrationEvent(RegistrationEvent event) {
        log.debug("处理活动报名付费: 用户ID={}, 金额={}, 活动ID={}, 描述={}",
                event.getRegistration().getUser().getId(),
                event.getRegistration().getFeeAmount(),
                event.getRegistration().getActivity().getId(),
                event.getDescription());
        try {
            // 处理扣款
            walletService.deduct(event.getRegistration().getUser().getId(), event.getRegistration().getFeeAmount());
            // 确认报名
            activityService.confirmRegistration(event.getRegistration());
        } catch (Exception e) {
            log.error("处理活动报名付费失败: 用户ID={}, 金额={}, 活动ID={}, 描述={}, 错误={}",
                    event.getRegistration().getUser().getId(),
                    event.getRegistration().getFeeAmount(),
                    event.getRegistration().getActivity().getId(),
                    event.getDescription(),
                    e.getMessage());
            // 如果扣款失败，取消报名
            activityService.cancelRegistration(event.getRegistration(), "报名付费失败，已取消报名");
        }
    }

    @EventListener
    public void handleUnregistrationEvent(UnregistrationEvent event) {
        log.debug("处理活动取消报名退款: 用户ID={}, 金额={}, 活动ID={}, 描述={}",
                event.getRegistration().getUser().getId(),
                event.getRegistration().getFeeAmount(),
                event.getRegistration().getActivity().getId(),
                event.getDescription());

        walletService.add(event.getRegistration().getUser().getId(), event.getRegistration().getFeeAmount());
    }
}