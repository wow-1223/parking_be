package com.parking.handler.payment.impl;

import com.google.gson.reflect.TypeToken;
import com.parking.config.WechatPayConfig;
import com.parking.constant.PayConstant;
import com.parking.enums.PayTypeEnum;
import com.parking.exception.PaymentException;
import com.parking.model.param.payment.request.PayRequest;
import com.parking.model.param.payment.response.PayResponse;
import com.parking.model.param.payment.response.WechatPayResponse;
import com.parking.handler.payment.PayService;
import com.parking.model.vo.pay.PayNotifyVO;
import com.parking.util.JsonUtil;
import com.parking.handler.WechatPayUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WechatPayServiceImpl implements PayService {

    @Autowired
    private WechatPayConfig wechatPayConfig;

    @Autowired
    private WechatPayUtil wechatPayUtil;

    private CloseableHttpClient wechatpayClient;

    @PostConstruct
    public void init() {
        try {
            // todo for test
            wechatpayClient = null;
//            // 加载商户私钥
//            PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
//                    new FileInputStream(wechatPayConfig.getPrivateKeyPath()));
//
//            // 创建签名器
//            PrivateKeySigner signer = new PrivateKeySigner(
//                    wechatPayConfig.getMchSerialNo(), merchantPrivateKey);
//
//            // 创建认证器
//            WechatPay2Credentials credentials = new WechatPay2Credentials(
//                    wechatPayConfig.getMchId(), signer);
//
//            // 创建支付客户端
//            wechatpayClient = WechatPayHttpClientBuilder.create()
//                    .withMerchant(wechatPayConfig.getMchId(), wechatPayConfig.getMchSerialNo(), merchantPrivateKey)
//                    .withValidator(response -> true)
//                    .withCredentials(credentials)
//                    .build();

        } catch (Exception e) {
            log.error("init wechat pay client failed:", e);
            throw new PaymentException("init wechat pay client failed:", e);
        }
    }

    @Override
    public PayResponse queryOrder(String orderId) {
        try {
            String url = String.format(
                    "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s?mchid=%s",
                    orderId, wechatPayConfig.getMchId());

            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Accept", "application/json");

            CloseableHttpResponse response = wechatpayClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> map = JsonUtil.fromJson(responseBody, type);

            PayResponse payResponse = new PayResponse();
            payResponse.setOrderId(orderId);
            payResponse.setStatus(mapTradeState((String) map.get("trade_state")));
            return payResponse;

        } catch (Exception e) {
            log.error("query wechat pay order failed:", e);
            throw new PaymentException("query wechat pay order failed:", e);
        }
    }

    @Override
    public PayResponse createOrder(PayRequest request) {
        try {
            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi");
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Content-Type", "application/json");

            // 构建请求参数
            Map<String, Object> requestBody = generateStringObjectMap(request);

            StringEntity entity = new StringEntity(JsonUtil.toJson(requestBody), "UTF-8");
            httpPost.setEntity(entity);

            CloseableHttpResponse response = wechatpayClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());
            Map<String, Object> responseMap = JsonUtil.fromMapJson(responseBody, String.class, Object.class);

            // 生成支付参数
            String prepayId = (String) responseMap.get("prepay_id");
            Map<String, String> payParams = wechatPayUtil.buildJsapiPayParams(prepayId);

            WechatPayResponse payResponse = new WechatPayResponse();
            payResponse.setOrderId(request.getOrderId());
            payResponse.setPrepayId(prepayId);
            payResponse.setPaySign(payParams.get("paySign"));
            payResponse.setNonceStr(payParams.get("nonceStr"));
            payResponse.setTimestamp(payParams.get("timeStamp"));
            return payResponse;

        } catch (Exception e) {
            log.error("create wechat pay order failed:", e);
            throw new PaymentException("create wechat pay order failed:", e);
        }
    }

    @Override
    public PayResponse refund(String orderId, Long amount, String reason) {
        try {
            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds");
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Content-Type", "application/json");

            Map<String, Object> requestBody = generateStringObjectMap(orderId, amount, reason);

            StringEntity entity = new StringEntity(JsonUtil.toJson(requestBody), "UTF-8");
            httpPost.setEntity(entity);

            CloseableHttpResponse response = wechatpayClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            PayResponse payResponse = new PayResponse();
            payResponse.setOrderId(orderId);
            payResponse.setStatus(PayConstant.PayStatus.REFUNDED);
            return payResponse;

        } catch (Exception e) {
            log.error("apply for wechat pay refund failed:", e);
            throw new PaymentException("apply for wechat pay refund failed:", e);
        }
    }

    @Override
    public void verifyNotifySign(Map<String, String> params) {
        try {
            // 验证签名
            String notifyData = params.get("notifyData");
            String signature = params.get("signature");
            String nonce = params.get("nonce");
            String timestamp = params.get("timestamp");
            String serialNumber = params.get("serialNumber");

            boolean isSignValid = wechatPayUtil.verifyNotifySign(
                    notifyData, signature, nonce, timestamp, serialNumber);
            if (!isSignValid) {
                throw new PaymentException("wechat pay notify sign verify failed");
            }

            // 解密回调通知数据
            Map<String, Object> notifyMap = JsonUtil.fromMapJson(notifyData, String.class, Object.class);
            Map<String, String> resource = (Map<String, String>) notifyMap.get("resource");
            String ciphertext = resource.get("ciphertext");
            String associatedData = resource.get("associated_data");
            String nonce2 = resource.get("nonce");

            String decryptedData = wechatPayUtil.decryptNotifyData(
                    associatedData, nonce2, ciphertext);

            // 解析解密后的数据
            Map<String, Object> decryptedMap = JsonUtil.fromMapJson(notifyData, String.class, Object.class);

            // 验证支付结果
            String tradeState = (String) decryptedMap.get("trade_state");
            if (!"SUCCESS".equals(tradeState)) {
                log.error("wechat pay failed，order id：{}，trade status：{}",
                        decryptedMap.get("out_trade_no"), tradeState);
                return;
            }

            // 验证支付金额
            Map<String, Object> amount = (Map<String, Object>) decryptedMap.get("amount");
            Long payerTotal = Long.parseLong(String.valueOf(amount.get("payer_total")));
            // TODO: 这里可以添加订单金额验证逻辑

            log.info("[wechat pay notify] decrypted data: {}", decryptedData);

            log.info("wechat pay notify verify succeed, order id：{}，trade status：{}, payer amount：{}",
                    decryptedMap.get("out_trade_no"),
                    decryptedMap.get("transaction_id"),
                    payerTotal);
        } catch (Exception e) {
            log.error("handle wechat pay notify failed:", e);
            throw new PaymentException("handle wechat pay notify failed:", e);
        }
    }

    @Override
    public PayNotifyVO generatePayNotifyVO(Map<String, String> params) {
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

        return new PayNotifyVO(orderId, tradeNo, status, amount, notifyTime, PayTypeEnum.WECHAT_PAY);
    }

    @Override
    public PayNotifyVO generateRefundNotifyVO(Map<String, String> params) {
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
            log.error("refund failed, order id: {}, refund status: {}", orderId, refundStatus);
            return null;
        }

        return new PayNotifyVO(orderId, tradeNo, PayConstant.PayStatus.REFUNDED, refundAmount, successTime, PayTypeEnum.WECHAT_PAY);
    }

    private Map<String, Object> generateStringObjectMap(PayRequest req) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("appid", wechatPayConfig.getAppId());
        requestBody.put("mchid", wechatPayConfig.getMchId());
        requestBody.put("description", req.getDescription());
        requestBody.put("out_trade_no", req.getOrderId());
        requestBody.put("notify_url", wechatPayConfig.getNotifyUrl());

        Map<String, Object> amount = new HashMap<>();
        amount.put("total", req.getAmount());
        amount.put("currency", "CNY");
        requestBody.put("amount", amount);

        Map<String, String> payer = new HashMap<>();
        payer.put("openid", req.getOpenid());
        requestBody.put("payer", payer);
        return requestBody;
    }

    @NotNull
    private Map<String, Object> generateStringObjectMap(String orderId, Long amount, String reason) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("out_trade_no", orderId);
        requestBody.put("out_refund_no", "REFUND_" + orderId);

        Map<String, Long> amountMap = new HashMap<>();
        amountMap.put("refund", amount);
        amountMap.put("total", amount);
        requestBody.put("amount", amountMap);

        requestBody.put("reason", reason);
        requestBody.put("notify_url", wechatPayConfig.getNotifyUrl() + "/refund");
        return requestBody;
    }

    /**
     * 映射微信支付状态
     */
    private String mapTradeState(String tradeState) {
        return switch (tradeState) {
            case "SUCCESS" -> PayConstant.PayStatus.SUCCESS;
            case "REFUND" -> PayConstant.PayStatus.REFUNDED;
            case "NOTPAY" -> PayConstant.PayStatus.PENDING;
            case "CLOSED" -> PayConstant.PayStatus.CLOSED;
            default -> PayConstant.PayStatus.FAILED;
        };
    }
}