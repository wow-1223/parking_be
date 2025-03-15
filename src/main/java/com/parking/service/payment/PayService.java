package com.parking.service.payment;

import com.parking.model.param.payment.request.PayRequest;
import com.parking.model.param.payment.response.PayResponse;
import com.parking.model.vo.pay.PayNotifyVO;
import java.util.Map;

public interface PayService {

    /**
     * 创建支付订单
     */
    PayResponse createOrder(PayRequest request);

    /**
     * 查询订单状态
     */
    PayResponse queryOrder(String orderId);

    /**
     * 申请退款
     */
    PayResponse refund(String orderId, Long amount, String reason);

    /**
     * 回调验签
     */
    void verifyNotifySign(Map<String, String> params);

    /**
     * 生成支付回调参数
     */
    PayNotifyVO generatePayNotifyVO(Map<String, String> params);

    /**
     * 生成退款回调参数
     */
    PayNotifyVO generateRefundNotifyVO(Map<String, String> params);
}