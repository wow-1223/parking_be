package com.parking.enums.sort;

import lombok.Getter;

@Getter
public enum SortOrderEnum {

    ASC("asc"),
    DESC( "desc");

    private final String value;


    SortOrderEnum(String value) {
        this.value = value;
    }

    public static SortOrderEnum getByValue(String value) {
        for (SortOrderEnum sortTypeEnum : SortOrderEnum.values()) {
            if (sortTypeEnum.getValue().equals(value)) {
                return sortTypeEnum;
            }
        }
        return null;
    }
}
