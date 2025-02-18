package com.parking.model.param.payment.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayResponse {

    /**
     * 商户订单号
     */
    private String orderId;

    /**
     * 交易状态
     */
    private String status;

}
