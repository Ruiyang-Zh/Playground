package cn.edu.nju.playground.enums;

public enum RegistrationStatus {
    PENDING("待处理"),
    CONFIRMED("已确认"),
    CANCELLED("已取消");

    private final String description;

    RegistrationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
