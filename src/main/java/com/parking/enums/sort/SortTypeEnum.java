package com.parking.enums.sort;

import lombok.Getter;

@Getter
public enum SortTypeEnum {

    PRICE( "price"),
    DISTANCE("distance"),
    RATING( "rating"),
    ;

    private final String value;

    SortTypeEnum(String value) {
        this.value = value;
    }

    public static SortTypeEnum from(String value) {
        for (SortTypeEnum e : SortTypeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
