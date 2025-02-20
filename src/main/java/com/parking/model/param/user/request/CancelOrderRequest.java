package com.parking.model.param.user.request;

import lombok.Data;

@Data
public class CancelOrderRequest {
    private Long userId;
    private Long orderId;
}
