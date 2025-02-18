package com.parking.enums.parking;

import lombok.Getter;

@Getter
public enum SpotModeEnum {

    SPECIFIC_DATE(0, "specific date"),
    DAILY(1, "daily"),
    WEEKLY(2, "weekly"),
    MONTHLY(3, "monthly");

    private final int mode;
    private final String description;

    SpotModeEnum(int mode, String description) {
        this.mode = mode;
        this.description = description;
    }

    public int getMode() {
        return mode;
    }

    public String getDescription() {
        return description;
    }

    // 根据 code 获取对应的枚举实例
    public static SpotModeEnum getByCode(int code) {
        for (SpotModeEnum status : values()) {
            if (status.mode == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching SpotMode enum found for code: " + code);
    }

    // 根据描述获取对应的枚举实例
    public static SpotModeEnum getByDescription(String description) {
        for (SpotModeEnum status : values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching SpotMode enum found for description: " + description);
    }

}
