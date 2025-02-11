package com.parking.model.dto.payment;

import lombok.Data;

@Data
public class WxPayResponse {

    // "预支付交易会话标识"
    private String prepayId;

    // "支付签名"
    private String paySign;

    // "随机字符串"
    private String nonceStr;

    // "时间戳"
    private String timeStamp;

    // "订单号"
    private String orderId;

    // "支付状态"
    private String status;
} 