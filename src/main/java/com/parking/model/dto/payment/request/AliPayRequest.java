package com.parking.model.dto.payment.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AliPayRequest extends PayRequest {

    /**
     * 商品描述
     */
    private String subject;

    /**
     * 商品编码
     */
    private String productCode;
}
