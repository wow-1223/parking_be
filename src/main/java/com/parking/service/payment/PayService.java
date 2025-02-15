package com.parking.service.payment;

import com.parking.model.dto.payment.request.PayRequest;
import com.parking.model.dto.payment.response.PayResponse;

import java.util.Map;

public interface PayService {

    /**
     * 创建支付订单
     */
    PayResponse createOrder(PayRequest request);

    /**
     * 处理支付回调
     */
    void handleNotify(Map<String, String> params);

    /**
     * 查询订单状态
     */
    PayResponse queryOrder(String orderId);

    /**
     * 申请退款
     */
    PayResponse refund(String orderId, Long amount, String reason);
}