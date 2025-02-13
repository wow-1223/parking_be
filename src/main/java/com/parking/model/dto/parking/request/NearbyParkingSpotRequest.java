package com.parking.model.dto.parking.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyParkingSpotRequest {
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer radius;
    private Long startTime;
    private Long endTime;
    private Integer page;
    private Integer size;
}
