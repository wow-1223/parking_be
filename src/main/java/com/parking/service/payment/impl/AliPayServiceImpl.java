package com.parking.service.payment.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.parking.config.AlipayConfig;
import com.parking.exception.PaymentException;
import com.parking.model.param.payment.request.PayRequest;
import com.parking.model.param.payment.response.AliPayResponse;
import com.parking.model.param.payment.response.PayResponse;
import com.parking.model.param.payment.request.AliPayRequest;
import com.parking.service.payment.PayService;
import com.parking.util.tool.JsonUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AliPayServiceImpl implements PayService {

    @Autowired
    private AlipayConfig alipayConfig;

    private AlipayClient alipayClient;

    @PostConstruct
    public void init() {
        // 初始化AlipayClient
        alipayClient = new DefaultAlipayClient(
                alipayConfig.getServerUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                alipayConfig.getFormat(),
                alipayConfig.getCharset(),
                alipayConfig.getPublicKey(),
                alipayConfig.getSignType()
        );
    }

    @Override
    public PayResponse createOrder(PayRequest request) {
        try {
            AlipayTradeWapPayRequest alipayRequest = generateAlipayTradeWapPayRequest((AliPayRequest) request);

            AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest);
            if (!response.isSuccess()) {
                throw new PaymentException("create ali pay order failed: " + response.getSubMsg());
            }

            AliPayResponse payResponse = new AliPayResponse();
            payResponse.setOrderId(request.getOrderId());
            payResponse.setPayUrl(response.getBody());
            return payResponse;
        } catch (AlipayApiException e) {
            log.error("create ali pay order failed: ", e);
            throw new PaymentException("create ali pay order failed: ", e);
        }
    }

    /**
     * 处理支付回调
     */
    @Override
    public void handleNotify(Map<String, String> params) {
        try {
            // 验证签名
            boolean signVerified = verifySign(params);
            if (!signVerified) {
                throw new PaymentException("ali pay notify sign verify failed");
            }

            // 处理支付结果
            String tradeStatus = params.get("trade_status");
            String orderId = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");

            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                // 更新订单状态
                updateOrderStatus(orderId, tradeNo, params);
            }

        } catch (Exception e) {
            log.error("handle ali pay notify failed:", e);
            throw new PaymentException("handle ali pay notify failed:", e);
        }
    }

    /**
     * 查询订单状态
     */
    @Override
    public PayResponse queryOrder(String orderId) {
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            Map<String, String> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderId);
            request.setBizContent(JsonUtil.toJson(bizContent));

            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                throw new PaymentException("query ali pay order failed:" + response.getSubMsg());
            }

            AliPayResponse payResponse = new AliPayResponse();
            payResponse.setOrderId(orderId);
            payResponse.setStatus(mapTradeStatus(response.getTradeStatus()));
            return payResponse;

        } catch (AlipayApiException e) {
            log.error("query ali pay order failed:", e);
            throw new PaymentException("query ali pay order failed:", e);
        }
    }

    /**
     * 申请退款
     */
    @Override
    public PayResponse refund(String orderId, Long amount, String reason) {
        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderId);
            bizContent.put("refund_amount", String.valueOf(amount / 100.0));
            bizContent.put("refund_reason", reason);
            request.setBizContent(JsonUtil.toJson(bizContent));

            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                throw new PaymentException("apply for ali pay refund failed:" + response.getSubMsg());
            }

            PayResponse payResponse = new PayResponse();
            payResponse.setOrderId(orderId);
            payResponse.setStatus("REFUNDED");
            return payResponse;

        } catch (AlipayApiException e) {
            log.error("apply for ali pay refund failed:", e);
            throw new PaymentException("apply for ali pay refund failed:", e);
        }
    }

    /**
     * 更新订单状态
     */
    private void updateOrderStatus(String orderId, String tradeNo, Map<String, String> params) {
        // TODO: 实现订单状态更新逻辑
    }

    @NotNull
    private AlipayTradeWapPayRequest generateAlipayTradeWapPayRequest(AliPayRequest request) {
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());

        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(request.getOrderId());
        model.setTotalAmount(String.valueOf(request.getAmount() / 100.0)); // 转换为元
        model.setSubject(request.getDescription());
//        model.setProductCode("QUICK_WAP_WAY");
        model.setProductCode(request.getProductCode());

        alipayRequest.setBizModel(model);
        return alipayRequest;
    }

    /**
     * 验证签名
     */
    private boolean verifySign(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getPublicKey(),
                    alipayConfig.getCharset(),
                    alipayConfig.getSignType()
            );
        } catch (AlipayApiException e) {
            log.error("验证支付宝签名失败", e);
            return false;
        }
    }

    /**
     * 映射支付宝交易状态
     */
    private String mapTradeStatus(String tradeStatus) {
        switch (tradeStatus) {
            case "TRADE_SUCCESS":
                return "SUCCESS";
            case "TRADE_CLOSED":
                return "CLOSED";
            case "TRADE_FINISHED":
                return "FINISHED";
            default:
                return "PENDING";
        }
    }
}