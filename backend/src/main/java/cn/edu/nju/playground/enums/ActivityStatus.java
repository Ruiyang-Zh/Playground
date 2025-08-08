package cn.edu.nju.playground.enums;

public enum ActivityStatus {
    RECRUITING("招募中"),
    FULL("已满员"),
    REGISTRATION_CLOSED("截止报名"),
    IN_PROGRESS("进行中"),
    COMPLETED("已结束"),
    CANCELLED("已取消");

    private final String description;

    ActivityStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
