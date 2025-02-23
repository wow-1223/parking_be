package com.parking.model.param.user.request;

import lombok.Data;

@Data
public class OperateOrderRequest {
    private Long userId;
    private Long orderId;
}
