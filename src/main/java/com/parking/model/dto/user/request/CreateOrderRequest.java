package com.parking.model.dto.user.request;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String parkingId;
    private String startTime;
    private String endTime;
    private String carNumber;
} 