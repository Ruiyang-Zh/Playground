package cn.edu.nju.playground.enums;

public enum SportsType {
    BASKETBALL("篮球"),
    BADMINTON("羽毛球"),
    RUNNING("跑步"),
    SWIMMING("游泳"),
    FOOTBALL("足球"),
    TENNIS("网球"),
    TABLE_TENNIS("乒乓球"),
    CYCLING("骑行"),
    HIKING("徒步"),
    YOGA("瑜伽"),
    FITNESS("健身"),
    OTHER("其他");

    private final String description;

    SportsType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
