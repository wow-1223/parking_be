package com.parking.enums.sort;

public enum SortTypeEnum {

    ALL(0, "all"),
    PRICE(1, "price"),
    DISTANCE(2, "distance"),
    RATING(3, "rating"),
    ;

    private final int type;
    private final String description;

    SortTypeEnum(int type, String description) {
        this.type = type;
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    // 根据 code 获取对应的枚举实例
    public static SortTypeEnum getByCode(int code) {
        for (SortTypeEnum sortTypeEnum : SortTypeEnum.values()) {
            if (sortTypeEnum.getType() == code) {
                return sortTypeEnum;
            }
        }
        return null;
    }

    // 根据 description 获取对应的枚举实例
    public static SortTypeEnum getByDescription(String description) {
        for (SortTypeEnum sortTypeEnum : SortTypeEnum.values()) {
            if (sortTypeEnum.getDescription().equals(description)) {
                return sortTypeEnum;
            }
        }
        return null;
    }
}
