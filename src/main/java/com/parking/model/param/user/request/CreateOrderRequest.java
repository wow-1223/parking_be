package com.parking.model.param.user.request;

import lombok.Data;

@Data
public class CreateOrderRequest {

    /**
     * 停车场id
     */
    private Long parkingSpotId;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 车牌号
     */
    private String carNumber;
}