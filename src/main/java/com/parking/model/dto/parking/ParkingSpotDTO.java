package com.parking.model.dto.parking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpotDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
    private String location;
    private BigDecimal price;
}
