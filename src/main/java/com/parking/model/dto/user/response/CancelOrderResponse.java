package com.parking.model.dto.user.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 取消订单响应DTO
 */
@Data
public class CancelOrderResponse {
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 取消原因
     */
    private String reason;

    /**
     * 取消时间
     */
    private String cancelTime;

    public static CancelOrderResponse success(BigDecimal refundAmount) {
        CancelOrderResponse response = new CancelOrderResponse();
        response.setSuccess(true);
        response.setRefundAmount(refundAmount);
        return response;
    }

    public static CancelOrderResponse fail(String reason) {
        CancelOrderResponse response = new CancelOrderResponse();
        response.setSuccess(false);
        response.setReason(reason);
        return response;
    }
}