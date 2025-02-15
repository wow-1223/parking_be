package com.parking.enums;

import lombok.Getter;

@Getter
public enum PayTypeEnum {

    WECHAT_PAY("wechat_pay"),
    ALIPAY("alipay");

    private final String value;

    PayTypeEnum(String value) {
        this.value = value;
    }

    public static PayTypeEnum fromValue(String value) {
        for (PayTypeEnum e : PayTypeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}
