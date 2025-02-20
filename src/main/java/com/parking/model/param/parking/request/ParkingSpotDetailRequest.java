package com.parking.model.param.parking.request;

import lombok.Data;

@Data
public class ParkingSpotDetailRequest {
    private Long id;
    private String startTime;
    private String endTime;
}
