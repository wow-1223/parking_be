package com.parking.util;

import com.parking.constant.PayConstant;
import com.parking.enums.PayTypeEnum;
import com.parking.event.PayNotifyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PayNotifyParser {

    private final WechatPayUtil wechatPayUtil;

    public PayNotifyParser(WechatPayUtil wechatPayUtil) {
        this.wechatPayUtil = wechatPayUtil;
    }

    /**
     * 解析支付宝回调参数
     */
    public Map<String, String> parseAlipayNotify(HttpServletRequest request) {
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
        return params;
    }

    /**
     * 解析微信支付回调参数
     */
    public Map<String, String> parseWechatPayNotify(String notifyData, String signature,
                                                    String nonce, String timestamp, String serialNumber) {
        Map<String, String> params = new HashMap<>();
        params.put("notifyData", notifyData);
        params.put("signature", signature);
        params.put("nonce", nonce);
        params.put("timestamp", timestamp);
        params.put("serialNumber", serialNumber);
        return params;
    }

    /**
     * 构建支付宝回调事件
     */
    public PayNotifyEvent buildAlipayNotifyEvent(Map<String, String> params) {
        String orderId = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        String totalAmount = params.get("total_amount");
        String notifyTime = params.get("notify_time");

        // 转换支付状态
        String status = "TRADE_SUCCESS".equals(tradeStatus) ?
                PayConstant.PayStatus.SUCCESS : PayConstant.PayStatus.PENDING;

        // 转换金额为分
        Long amount = Math.round(Double.parseDouble(totalAmount) * 100);

        return new PayNotifyEvent(this, orderId, tradeNo,
                status, amount, notifyTime, PayTypeEnum.ALIPAY);
    }

    /**
     * 构建微信回调事件
     */
    public PayNotifyEvent buildWechatPayNotifyEvent(Map<String, String> params) {
        // 解析微信支付通知数据
        String notifyData = params.get("notifyData");
        Map<String, Object> notifyMap = JsonUtil.fromMapJson(notifyData, String.class, Object.class);

        String orderId = (String) notifyMap.get("out_trade_no");
        String tradeNo = (String) notifyMap.get("transaction_id");
        String tradeStatus = (String) notifyMap.get("trade_state");
        Long amount = Long.parseLong(String.valueOf(notifyMap.get("amount")));
        String notifyTime = (String) notifyMap.get("success_time");

        // 转换支付状态
        String status = "SUCCESS".equals(tradeStatus) ?
                PayConstant.PayStatus.SUCCESS : PayConstant.PayStatus.PENDING;

        return new PayNotifyEvent(this, orderId, tradeNo,
                status, amount, notifyTime, PayTypeEnum.WECHAT_PAY);
    }

    /**
     * 构建支付宝退款回调事件
     */
    public PayNotifyEvent buildAlipayRefundNotifyEvent(Map<String, String> params) {
        String orderId = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String refundAmount = params.get("refund_amount");
        String gmtRefund = params.get("gmt_refund");

        // 转换金额为分
        Long amount = Math.round(Double.parseDouble(refundAmount) * 100);

        return new PayNotifyEvent(this, orderId, tradeNo,
                PayConstant.PayStatus.REFUNDED, amount, gmtRefund, PayTypeEnum.ALIPAY);
    }

    /**
     * 构建微信支付退款回调事件
     */
    public PayNotifyEvent buildWechatPayRefundNotifyEvent(Map<String, String> params) {
        // 解析微信支付通知数据
        String notifyData = params.get("notifyData");
        Map<String, Object> notifyMap = JsonUtil.fromMapJson(notifyData, String.class, Object.class);

        // 解密回调数据
        Map<String, String> resource = (Map<String, String>) notifyMap.get("resource");
        String decryptedData = wechatPayUtil.decryptNotifyData(
                resource.get("associated_data"),
                resource.get("nonce"),
                resource.get("ciphertext")
        );

        // 解析解密后的数据
        Map<String, Object> refundInfo = JsonUtil.fromMapJson(decryptedData, String.class, Object.class);

        // 从解密后的数据中获取退款信息
        String orderId = (String) refundInfo.get("out_trade_no");
        String tradeNo = (String) refundInfo.get("transaction_id");
        String refundStatus = (String) refundInfo.get("refund_status");
        String successTime = (String) refundInfo.get("success_time");

        // 获取退款金额（单位：分）
        Map<String, Object> amount = (Map<String, Object>) refundInfo.get("amount");
        Long refundAmount = Long.parseLong(String.valueOf(amount.get("refund")));

        // 只有退款成功才发送退款事件
        if (!"SUCCESS".equals(refundStatus)) {
            log.warn("refund failed, order id: {}, refund status: {}", orderId, refundStatus);
            return null;
        }

        return new PayNotifyEvent(this, orderId, tradeNo,
                PayConstant.PayStatus.REFUNDED, refundAmount, successTime, PayTypeEnum.WECHAT_PAY);
    }
}