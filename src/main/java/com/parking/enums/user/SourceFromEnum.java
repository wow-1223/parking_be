package com.parking.enums.user;

import lombok.Getter;

@Getter
public enum SourceFromEnum {

    WECHAT(1, "wechat"),
    APP(2, "app");

    private final int sourceFrom;
    private final String description;

    SourceFromEnum(int sourceFrom, String description) {
        this.sourceFrom = sourceFrom;
        this.description = description;
    }

    // 根据 code 获取对应的枚举实例
    public static SourceFromEnum getByCode(int code) {
        for (SourceFromEnum sourceFrom : values()) {
            if (sourceFrom.sourceFrom == code) {
                return sourceFrom;
            }
        }
        throw new IllegalArgumentException("No matching SourceFrom enum found for code: " + code);
    }

    // 根据描述获取对应的枚举实例
    public static SourceFromEnum getByDescription(String description) {
        for (SourceFromEnum sourceFrom : values()) {
            if (sourceFrom.description.equals(description)) {
                return sourceFrom;
            }
        }
        throw new IllegalArgumentException("No matching SourceFrom enum found for description: " + description);
    }       
}
