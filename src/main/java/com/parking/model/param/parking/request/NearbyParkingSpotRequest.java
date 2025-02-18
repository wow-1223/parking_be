package com.parking.model.param.parking.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyParkingSpotRequest {
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private BigDecimal price;
    private String startTime;
    private String endTime;
    private Integer page;
    private Integer size;
}
