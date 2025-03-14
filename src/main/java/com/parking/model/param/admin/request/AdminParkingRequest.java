package com.parking.model.param.admin.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminParkingRequest {
    /**
     * 所有者用户id
     */
    private Long ownerId;
    /**
     * 地址
     */
    private String location;
    /**
     * 最高价格
     */
    private BigDecimal maxPrice;
    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 停车场状态
     */
    private Integer status;
    /**
     * 停车场类型
     */
    private String parkingType;
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
