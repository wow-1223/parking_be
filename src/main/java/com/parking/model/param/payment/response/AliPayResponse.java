package com.parking.model.param.payment.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AliPayResponse extends PayResponse {

    /**
     * 支付表单地址
     */
    private String payUrl;
}
