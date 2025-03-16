package com.parking.enums.lock;

import lombok.Getter;

@Getter
public enum LockStatusEnum {

    FALLEN("fallen"),
    RAISED("raised"),
    BROKEN("broken"),
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
        return BROKEN;
    }
}
