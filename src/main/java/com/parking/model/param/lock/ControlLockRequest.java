package com.parking.model.param.lock;

import lombok.Data;

@Data
public class ControlLockRequest {

    private String deviceId;
    private String command;
    private String operator;
}
