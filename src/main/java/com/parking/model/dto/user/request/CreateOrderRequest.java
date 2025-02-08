package com.parking.model.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String parkingId;
    private String startTime;
    private String endTime;
    private String carNumber;
} 