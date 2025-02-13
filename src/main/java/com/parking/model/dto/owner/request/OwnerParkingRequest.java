package com.parking.model.dto.owner.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OwnerParkingRequest {
    private String location;
    private Double latitude;
    private Double longitude;
    private String description;
    private BigDecimal price;
    private List<String> images;
    private List<String> rules;
    private List<String> facilities;
} 