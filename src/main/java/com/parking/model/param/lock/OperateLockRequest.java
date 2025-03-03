package com.parking.model.param.lock;

import lombok.Data;

@Data
public class OperateLockRequest {

    private String deviceId;
    private String status;
    private String timestamp;

}
