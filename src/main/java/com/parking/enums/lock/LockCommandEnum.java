package com.parking.enums.lock;

import lombok.Getter;

@Getter
public enum LockCommandEnum {

    FALL("fall"),
    RISE("rise"),
    STOP("stop"),
    ;

    private final String command;

    LockCommandEnum(String command) {
        this.command = command;
    }

    public static LockCommandEnum fromCommand(String command) {
        for (LockCommandEnum lockCommandEnum : LockCommandEnum.values()) {
            if (lockCommandEnum.getCommand().equals(command)) {
                return lockCommandEnum;
            }
        }
        return null;
    }
}
