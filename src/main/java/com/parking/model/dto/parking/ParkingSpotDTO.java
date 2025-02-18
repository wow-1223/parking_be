package com.parking.model.dto.parking;

import com.parking.model.vo.parking.rule.ParkingSpotRuleVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpotDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
    private String location;
    private String description;
    private BigDecimal price;
    private List<String> images;
    private List<String> facilities;
    private List<ParkingSpotRuleVO> rules;
}
