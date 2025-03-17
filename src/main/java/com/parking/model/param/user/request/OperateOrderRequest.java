package com.parking.model.param.user.request;

import lombok.Data;

@Data
public class OperateOrderRequest {
    // private Long userId;
    // null when create
    private Long orderId;
    private Long parkingSpotId;
    private String startTime;
    private String endTime;
    private String carNumber;
}