package com.parking.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ParkingSpotDTO {
    private String id;
    private String location;
    private Double latitude;
    private Double longitude;
    private BigDecimal price;
    private String image;
    private String status;
} 