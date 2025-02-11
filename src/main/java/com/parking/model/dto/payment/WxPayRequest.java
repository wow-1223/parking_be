package com.parking.model.dto.payment;

import lombok.Data;

@Data
public class WxPayRequest {

    // "订单ID", required = true
    private String orderId;

    // "支付金额(分)", required = true
    private Long amount;

    // "商品描述", required = true
    private String description;

    // "用户openid", required = true
    private String openid;
} 