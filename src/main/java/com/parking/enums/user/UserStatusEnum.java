package com.parking.enums.user;

import lombok.Getter;

@Getter
public enum UserStatusEnum {

    ACTIVE(1, "active"),
    DISABLED(2, "disabled"),
    VIOLATED(3, "violated"),
    ;

    private final int status;
    private final String description;

    UserStatusEnum(int status, String description) {
        this.status = status;
        this.description = description;
    }

    // 根据 code 获取对应的枚举实例
    public static UserStatusEnum getByCode(int code) {
        for (UserStatusEnum status : values()) {
            if (status.status == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching ParkingSpotStatus enum found for code: " + code);
    }

    // 根据描述获取对应的枚举实例
    public static UserStatusEnum getByDescription(String description) {
        for (UserStatusEnum status : values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching ParkingSpotStatus enum found for description: " + description);
    }
}
