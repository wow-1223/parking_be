package com.parking.model.param.user.request;

import lombok.Data;

@Data
public class CreateOrderRequest {
    // private Long userId;
    private Long parkingSpotId;
    private String startTime;
    private String endTime;
    private String carNumber;
}