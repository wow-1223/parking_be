package com.parking.model.param.owner.request;

import com.parking.model.vo.parking.ParkingSpotRuleStrVO;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OwnerParkingRequest {
//    private Long userId;
    private String location;
    private Double latitude;
    private Double longitude;
    private String description;
    private BigDecimal price;
    private List<String> images;
    private List<ParkingSpotRuleStrVO> rules;
    private List<String> facilities;
} 