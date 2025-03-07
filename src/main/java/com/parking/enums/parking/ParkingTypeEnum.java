package com.parking.enums.parking;

import lombok.Getter;

/**
 * 停车场类型枚举
 */
@Getter
public enum ParkingTypeEnum {

    /**
     * 地下停车场
     */
    UNDERGROUND(0, "underground"),

    /**
     * 地上停车场
     */
    GROUND(1, "ground"),

    /**
     * 立体停车场
     */
    ELEVATED(2, "elevated"),
    ;
    private final int code;
    private final String description;

    ParkingTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ParkingTypeEnum getByCode(int code) {
        for (ParkingTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching ParkingType enum found for code: " + code);
    }

    public static ParkingTypeEnum getByDescription(String description) {
        for (ParkingTypeEnum type : values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching ParkingType enum found for description: " + description);
    }

    public static Integer getCodeByDescription(String description) {
        if (description == null) {
            return null;
        }

        for (ParkingTypeEnum type : values()) {
            if (type.description.equals(description)) {
                return type.code;
            }
        }
        throw new IllegalArgumentException("No matching ParkingType enum found for description: " + description);
    }
}
