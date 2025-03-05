package com.parking.model.param.owner.request;

import com.parking.model.vo.parking.ParkingSpotRuleStrVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OwnerParkingRequest {

    /**
     * 停车场名称
     */
    private String location;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 车位描述
     */
    private String description;
    /**
     * 车位价格
     */
    private BigDecimal price;
    /**
     * 车位图片
     */
    private List<String> images;
    /**
     * 车位规则
     */
    private List<ParkingSpotRuleStrVO> rules;
    /**
     * 车位设施
     */
    private List<String> facilities;
} 