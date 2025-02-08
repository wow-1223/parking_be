package com.parking.model.dto.owner;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateParkingRequest {
    private String location;
    private String description;
    private BigDecimal price;
    private List<String> images;
    private List<String> rules;
    private List<String> facilities;
} 