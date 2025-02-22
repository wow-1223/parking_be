package com.parking.model.param.payment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayRequest {

//    private String appId;

    private String orderId;

    private Long amount;

    private String description;

    private String payType;


    // ali pay
    /**
     * 商品描述
     */
    private String subject;

    /**
     * 商品编码
     */
    private String productCode;

    // end ali pay

    // wechat

    /**
     * 微信用户openid
     */
    private String openid;

    // end wechat
}
