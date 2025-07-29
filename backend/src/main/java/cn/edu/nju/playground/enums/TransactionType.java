package cn.edu.nju.playground.enums;

public enum TransactionType {
    RECHARGE("充值"),
    WITHDRAW("提现"),
    ACTIVITY_FEE("活动费用"),
    REFUND("退款"),
    EARNINGS("收入");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}