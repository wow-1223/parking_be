package com.parking.model.dto.payment.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WechatPayRequest extends PayRequest {

    /**
     * 微信用户openid
     */
    private String openid;

}