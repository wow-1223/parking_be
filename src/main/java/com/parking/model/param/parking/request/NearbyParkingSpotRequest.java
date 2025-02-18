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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer page;
    private Integer size;
}
