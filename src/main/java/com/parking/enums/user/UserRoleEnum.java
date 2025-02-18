package com.parking.enums.user;

import lombok.Getter;

@Getter
public enum UserRoleEnum {

    USER(1, "user"),
    OWNER(2, "owner"),
    ADMIN(3, "admin");


    private final int role;
    private final String description;

    UserRoleEnum(int role, String description) {
        this.role = role;
        this.description = description;
    }

    // 根据 code 获取对应的枚举实例
    public static UserRoleEnum getByCode(int code) {
        for (UserRoleEnum role : values()) {
            if (role.role == code) {
                return role;
            }
        }
        return null;
    }

    // 根据 description 获取对应的枚举实例
    public static UserRoleEnum getByDescription(String description) {
        for (UserRoleEnum role : values()) {
            if (role.description.equals(description)) {
                return role;
            }
        }
        return null;
    }

}
