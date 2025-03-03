package com.parking.model.param.lock;

import lombok.Data;

@Data
public class BindLockRequest {

    private String deviceId;
    private String parkingSpotId;

}
