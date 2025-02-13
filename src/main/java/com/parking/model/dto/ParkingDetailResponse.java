package com.parking.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ParkingDetailResponse {
    private String id;
    private String location;
    private String description;
    private List<String> images;
    private BigDecimal price;
    private String status;
    
    private OwnerInfo owner;
    private List<String> rules;
    private List<String> facilities;
    
    @Data
    public static class OwnerInfo {
        private String id;
        private String name;
        private String phone;
        private Double rating;
    }
} 