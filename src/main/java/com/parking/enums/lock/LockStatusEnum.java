package com.parking.enums.lock;

import lombok.Getter;

@Getter
public enum LockStatusEnum {

    LOWERED("lowered"),
    RAISED("raised"),
    UNKNOWN("unknown"),
    ;

    private final String status;

    LockStatusEnum(String status) {
        this.status = status;
    }

    public static LockStatusEnum getEnumByStatus(String status) {
        for (LockStatusEnum lockStatusEnum : LockStatusEnum.values()) {
            if (lockStatusEnum.getStatus().equals(status)) {
                return lockStatusEnum;
            }
        }
        return UNKNOWN;
    }
}
