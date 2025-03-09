package com.parking.enums.order;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    PENDING_PAYMENT(0, "pending payment"),
    RESERVED(1, "reserved"),
    CONFIRMED(2, "confirmed"),
    PROCESSING(3, "processing"),
    COMPLETED(4, "completed"),
    CANCELING(5, "canceling"),
    CANCELED(6, "canceled"),
    REFUNDING(7, "refunding"),
    REFUNDED(8, "refunded"),
    TIMEOUT(9, "timeout"),
    TIMEOUT_PENDING_PAYMENT(10, "timeout pending payment"),
//    TIMEOUT_PAID(11, "timeout paid"),
    LEAVE_TEMPORARILY(11, "leave temporarily"),
    USER_OCCUPIED(12, "user occupied"),
    UNKNOWN_OCCUPIED(13, "unknown occupied")
    ;

    private final int status;
    private final String description;

    OrderStatusEnum(int status, String description) {
        this.status = status;
        this.description = description;
    }

    // 根据 code 获取对应的枚举实例
    public static OrderStatusEnum getByCode(int code) {
        for (OrderStatusEnum status : values()) {
            if (status.status == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching OrderStatus enum found for code: " + code);
    }

    // 根据描述获取对应的枚举实例
    public static OrderStatusEnum getByDescription(String description) {
        for (OrderStatusEnum status : values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching OrderStatus enum found for description: " + description);
    }
}
