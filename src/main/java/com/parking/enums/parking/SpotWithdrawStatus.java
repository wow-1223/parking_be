package com.parking.enums.parking;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum SpotWithdrawStatus {

    /**
     * 处理成功
     */
    SUCCESS("success"),
    /**
     * 处理失败
     */
    FAIL("fail");

    private final String status;

    SpotWithdrawStatus(String status) {
        this.status = status;
    }

    public static SpotWithdrawStatus fromStatus(String status) {
        for (SpotWithdrawStatus s : SpotWithdrawStatus.values()) {
            if (Objects.equals(s.status, status)) {
                return s;
            }
        }
        return null;
    }
}
