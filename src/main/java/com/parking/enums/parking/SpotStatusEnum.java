package com.parking.enums.parking;

import lombok.Getter;

@Getter
public enum SpotStatusEnum {

//    PENDING(0, "pending"),
    APPROVING(1, "approving"),
    AVAILABLE(2, "available"),
    REJECTED(3, "rejected");

    private final int status;
    private final String description;

    SpotStatusEnum(int status, String description) {
        this.status = status;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    // 根据 code 获取对应的枚举实例
    public static SpotStatusEnum getByCode(int code) {
        for (SpotStatusEnum status : values()) {
            if (status.status == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching ParkingSpotStatus enum found for code: " + code);
    }

    // 根据描述获取对应的枚举实例
    public static SpotStatusEnum getByDescription(String description) {
        for (SpotStatusEnum status : values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching ParkingSpotStatus enum found for description: " + description);
    }
}
