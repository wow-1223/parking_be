package com.parking.service.payment;

import com.parking.config.WxPayConfig;
import com.parking.constant.WxPayConstant;
import com.parking.exception.PaymentException;
import com.parking.model.dto.payment.WxPayRequest;
import com.parking.model.dto.payment.WxPayResponse;
import com.parking.util.JsonUtil;
import com.parking.util.WxPayUtil;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

import com.parking.util.HttpClientUtil;

@Slf4j
@Service
public class WxPayService {

    @Autowired
    private WxPayConfig wxPayConfig;

    @Autowired
    private WxPayUtil wxPayUtil;

    /**
     * 创建支付订单
     */
    public WxPayResponse createOrder(WxPayRequest request) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            // 加载商户私钥
            PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
                    new FileInputStream(wxPayConfig.getPrivateKeyPath()));

            // 创建支付客户端
            httpClient = WechatPayHttpClientBuilder.create()
                    .withMerchant(wxPayConfig.getMchId(), wxPayConfig.getMchSerialNo(), merchantPrivateKey)
                    .withValidator(resp -> true)
                    .build();

            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi");
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Content-Type", "application/json");

            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("appid", wxPayConfig.getAppId());
            requestBody.put("mchid", wxPayConfig.getMchId());
            requestBody.put("description", request.getDescription());
            requestBody.put("out_trade_no", request.getOrderId());
            requestBody.put("notify_url", wxPayConfig.getNotifyUrl());

            Map<String, Object> amount = new HashMap<>();
            amount.put("total", request.getAmount());
            amount.put("currency", "CNY");
            requestBody.put("amount", amount);

            Map<String, String> payer = new HashMap<>();
            payer.put("openid", request.getOpenid());
            requestBody.put("payer", payer);

            // 发送请求
            StringEntity entity = new StringEntity(JsonUtil.toJson(requestBody), "UTF-8");
            httpPost.setEntity(entity);

            response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            // 解析响应
            Map<String, Object> responseMap = JsonUtil.fromJson(responseBody, Map.class);
            String prepayId = (String) responseMap.get("prepay_id");

            // 生成支付参数
            String nonceStr = wxPayUtil.generateNonceStr();
            String timestamp = wxPayUtil.generateTimeStamp();
            String paySign = wxPayUtil.generateSignature(prepayId, nonceStr, timestamp);

            // 构建响应
            WxPayResponse wxPayResponse = new WxPayResponse();
            wxPayResponse.setPrepayId(prepayId);
            wxPayResponse.setNonceStr(nonceStr);
            wxPayResponse.setTimeStamp(timestamp);
            wxPayResponse.setPaySign(paySign);
            wxPayResponse.setOrderId(request.getOrderId());
            wxPayResponse.setStatus(WxPayConstant.PayStatus.PENDING);

            return wxPayResponse;

        } catch (Exception e) {
            log.error("创建支付订单失败", e);
            throw new PaymentException("创建支付订单失败", e);
        } finally {
            HttpClientUtil.closeQuietly(response);
            HttpClientUtil.closeQuietly(httpClient);
        }
    }

    /**
     * 处理支付回调
     */
    public void handlePayNotify(String notifyData, String signature, String nonce, String timestamp, String serialNumber) {
        try {
            // 验证签名
            if (!wxPayUtil.verifyNotifySign(serialNumber, notifyData, signature, nonce, timestamp)) {
                log.error("支付回调签名验证失败");
                throw new PaymentException("签名验证失败");
            }

            // 解密回调数据
            Map<String, Object> notifyMap = JsonUtil.fromJson(notifyData, Map.class);
            String resource = JsonUtil.toJson(notifyMap.get("resource"));
            Map<String, String> resourceMap = JsonUtil.fromJson(resource, Map.class);

            String decryptData = wxPayUtil.decryptNotifyData(
                    resourceMap.get("associated_data"),
                    resourceMap.get("nonce"),
                    resourceMap.get("ciphertext")
            );

            // 解析支付结果
            Map<String, Object> resultMap = JsonUtil.fromJson(decryptData, Map.class);
            String orderId = (String) resultMap.get("out_trade_no");
            String tradeState = (String) resultMap.get("trade_state");
            String transactionId = (String) resultMap.get("transaction_id");

            // 更新订单状态
            updateOrderStatus(orderId, tradeState, transactionId, resultMap);

        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            throw new PaymentException("处理支付回调失败", e);
        }
    }

    /**
     * 更新订单状态
     */
    private void updateOrderStatus(String orderId, String tradeState, String transactionId, Map<String, Object> paymentInfo) {
        // 1. 查询订单
//        Order order = orderMapper.selectById(orderId);
//        if (order == null) {
//            throw new PaymentException("订单不存在: " + orderId);
//        }
//
//        // 2. 检查订单状态
//        if (order.getStatus().equals(OrderStatus.PAID)) {
//            log.info("订单已支付，忽略重复回调: {}", orderId);
//            return;
//        }
//
//        // 3. 更新订单状态
//        OrderUpdateDTO updateDTO = new OrderUpdateDTO();
//        updateDTO.setOrderId(orderId);
//        updateDTO.setStatus(mapTradeState(tradeState));
//        updateDTO.setTransactionId(transactionId);
//
//        // 获取支付金额
//        Map<String, Object> amount = (Map<String, Object>) paymentInfo.get("amount");
//        updateDTO.setPaymentAmount((Integer) amount.get("total"));
//        updateDTO.setPaymentTime(new Date());
//
//        // 4. 执行更新
//        try {
//            orderMapper.updateOrderStatus(updateDTO);
//
//            // 5. 发送订单状态变更事件
//            OrderStatusChangeEvent event = new OrderStatusChangeEvent(
//                    orderId, updateDTO.getStatus(), paymentInfo);
//            eventPublisher.publishEvent(event);
//
//        } catch (Exception e) {
//            log.error("更新订单状态失败: {}", orderId, e);
//            throw new PaymentException("更新订单状态失败");
//        }
    }

    /**
     * 查询支付订单
     */
    public WxPayResponse queryOrder(String orderId) {
        try {
            // 加载商户私钥
            PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
                    new FileInputStream(wxPayConfig.getPrivateKeyPath()));

            // 创建支付客户端
            CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
                    .withMerchant(wxPayConfig.getMchId(), wxPayConfig.getMchSerialNo(), merchantPrivateKey)
                    .withValidator(response -> true)
                    .build();

            String url = String.format("https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s?mchid=%s",
                    orderId, wxPayConfig.getMchId());

            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Accept", "application/json");

            CloseableHttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity());

            // 解析响应
            Map<String, Object> responseMap = JsonUtil.fromJson(responseBody, Map.class);

            WxPayResponse wxPayResponse = new WxPayResponse();
            wxPayResponse.setOrderId(orderId);
            wxPayResponse.setStatus(mapTradeState((String) responseMap.get("trade_state")));

            return wxPayResponse;
        } catch (Exception e) {
            log.error("查询支付订单失败", e);
            throw new PaymentException("查询支付订单失败", e);
        }
    }

    /**
     * 申请退款
     */
    public WxPayResponse refund(String orderId, Long amount, String reason) {
        try {
            // 加载商户私钥
            PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
                    new FileInputStream(wxPayConfig.getPrivateKeyPath()));

            // 创建支付客户端
            CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
                    .withMerchant(wxPayConfig.getMchId(), wxPayConfig.getMchSerialNo(), merchantPrivateKey)
                    .withValidator(response -> true)
                    .build();

            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds");
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Content-Type", "application/json");

            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("out_trade_no", orderId);
            requestBody.put("out_refund_no", generateRefundNo());

            Map<String, Object> amountMap = new HashMap<>();
            amountMap.put("refund", amount);
            amountMap.put("total", amount);
            amountMap.put("currency", "CNY");
            requestBody.put("amount", amountMap);

            requestBody.put("reason", reason);
            requestBody.put("notify_url", wxPayConfig.getNotifyUrl() + "/refund");

            StringEntity entity = new StringEntity(JsonUtil.toJson(requestBody), "UTF-8");
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            // 解析响应
            Map<String, Object> responseMap = JsonUtil.fromJson(responseBody, Map.class);

            WxPayResponse wxPayResponse = new WxPayResponse();
            wxPayResponse.setOrderId(orderId);
            wxPayResponse.setStatus(WxPayConstant.PayStatus.REFUNDED);

            return wxPayResponse;
        } catch (Exception e) {
            log.error("申请退款失败", e);
            throw new PaymentException("申请退款失败", e);
        }
    }

    /**
     * 生成退款单号
     */
    private String generateRefundNo() {
        return "REFUND_" + System.currentTimeMillis() + "_" + wxPayUtil.generateNonceStr().substring(0, 8);
    }

    /**
     * 映射微信支付状态到系统状态
     */
    private String mapTradeState(String tradeState) {
        switch (tradeState) {
            case "SUCCESS":
                return WxPayConstant.PayStatus.SUCCESS;
            case "REFUND":
                return WxPayConstant.PayStatus.REFUNDED;
            case "NOTPAY":
                return WxPayConstant.PayStatus.PENDING;
            case "CLOSED":
                return WxPayConstant.PayStatus.CLOSED;
            default:
                return WxPayConstant.PayStatus.FAILED;
        }
    }
}