package com.parking.model.dto.payment.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WechatPayResponse extends PayResponse {

    /**
     * 预支付交易会话标识
     */
    private String prepayId;

    /**
     * 支付请求的随机字符串
     */
    private String nonceStr;

    /**
     * 支付请求的时间戳
     */
    private String timestamp;

    /**
     * 支付请求的签名算法
     */
    private String signature;

    /**
     * 支付请求的签名
     */
    private String paySign;

    /**
     * 支付请求的签名类型
     */
    private String signType;
} 