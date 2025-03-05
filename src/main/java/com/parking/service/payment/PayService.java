package com.parking.service.payment;

import com.parking.model.param.payment.request.PayRequest;
import com.parking.model.param.payment.response.PayResponse;
import com.parking.model.vo.pay.PayNotifyVO;
import com.parking.util.DateUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
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

    /**
     * 解析支付宝回调参数
     */
    default Map<String, String> parseAlipayNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        if (params.get("notify_time") == null) {
            params.put("notify_time", DateUtil.formatTime(DateUtil.getCurrentDateTime()));
        }
        return params;
    }

    /**
     * 解析微信支付回调参数
     */
    default Map<String, String> parseWechatPayNotify(String notifyData, String signature,
                                                    String nonce, String timestamp, String serialNumber) {
        Map<String, String> params = new HashMap<>();
        params.put("notifyData", notifyData);
        params.put("signature", signature);
        params.put("nonce", nonce);
        params.put("timestamp", timestamp);
        params.put("serialNumber", serialNumber);
        return params;
    }
}