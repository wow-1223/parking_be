package com.parking.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ParkingSpotDTO {
    private String id;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal price;
    private List<String> images;
    private String status;
} 