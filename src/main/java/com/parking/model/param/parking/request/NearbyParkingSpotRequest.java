package com.parking.model.param.parking.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyParkingSpotRequest {
    /**
     * 经纬度
     */
    private Double latitude;
    /**
     * 经纬度
     */
    private Double longitude;
    /**
     * 半径
     */
    private Integer radius;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 停车场类型
     */
    private String parkingType;

    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 页数
     */
    private Integer page;
    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 排序类型
     */
    private String sortType;

    /**
     * 排序方式 true 升序 false 降序
     */
    private Boolean sortOrder;
}
