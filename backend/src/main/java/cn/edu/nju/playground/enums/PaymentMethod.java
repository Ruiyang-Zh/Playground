package cn.edu.nju.playground.enums;

public enum PaymentMethod {
    ALIPAY("支付宝");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
