package cn.edu.nju.playground.service;

import cn.edu.nju.playground.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public BigDecimal getBalance() {
        // TODO: 实现查询余额逻辑
        return BigDecimal.ZERO;
    }

    public void recharge(Long userId, BigDecimal amount) {
        // TODO: 实现充值逻辑
    }

    public void withdraw(Long userId, BigDecimal amount) {
        // TODO: 实现提现逻辑
    }

    // 业务内部调用方法
    public void deduct(Long userId, BigDecimal amount) {
        // TODO: 实现扣款逻辑
    }

    // 业务内部调用方法
    public void add(Long userId, BigDecimal amount) {
        // TODO: 实现增加余额逻辑
    }
}
