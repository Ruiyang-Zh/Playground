package cn.edu.nju.playground.enums;

public enum TransactionStatus {
    PENDING("处理中"),
    SUCCESS("成功"),
    FAILED("失败"),
    CANCELLED("已取消");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}