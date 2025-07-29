package cn.edu.nju.playground.enums;

public enum RegistrationStatus {
    CONFIRMED("确认报名"),
    WAITLIST("候补中"),
    CANCELLED("已取消");

    private final String description;

    RegistrationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
