package com.parking.model.dto.owner;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OwnerOrderListItemDTO {
    private String id;
    private ParkingSpotInfo parkingSpot;
    private UserInfo user;
    private String startTime;
    private String endTime;
    private BigDecimal amount;
    private String status;
    private String carNumber;
    
    @Data
    public static class ParkingSpotInfo {
        private String id;
        private String location;
    }
    
    @Data
    public static class UserInfo {
        private String id;
        private String name;
        private String phone;
    }
} 