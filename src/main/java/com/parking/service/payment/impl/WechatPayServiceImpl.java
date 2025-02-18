package com.parking.service.payment.impl;

import com.parking.config.WechatPayConfig;
import com.parking.constant.PayConstant;
import com.parking.exception.PaymentException;
import com.parking.model.param.payment.request.PayRequest;
import com.parking.model.param.payment.request.WechatPayRequest;
import com.parking.model.param.payment.response.PayResponse;
import com.parking.model.param.payment.response.WechatPayResponse;
import com.parking.service.payment.PayService;
import com.parking.util.tool.JsonUtil;
import com.parking.util.WechatPayUtil;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
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

import java.io.FileInputStream;
import java.security.PrivateKey;
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
    public PayResponse createOrder(PayRequest request) {
        try {
            WechatPayRequest req = (WechatPayRequest) request;
            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi");
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Content-Type", "application/json");

            // 构建请求参数
            Map<String, Object> requestBody = generateStringObjectMap(req);

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
    public void handleNotify(Map<String, String> params) {
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
                throw new PaymentException(PayConstant.PayError.INVALID_SIGN, "wechat pay notify sign verify failed");
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
                log.warn("wechat pay failed，order id：{}，trade status：{}",
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
    public PayResponse queryOrder(String orderId) {
        try {
            String url = String.format(
                    "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s?mchid=%s",
                    orderId, wechatPayConfig.getMchId());

            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Accept", "application/json");

            CloseableHttpResponse response = wechatpayClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());
            Map<String, Object> responseMap = JsonUtil.fromJson(responseBody, Map.class);

            PayResponse payResponse = new PayResponse();
            payResponse.setOrderId(orderId);
            payResponse.setStatus(mapTradeState((String) responseMap.get("trade_state")));
            return payResponse;

        } catch (Exception e) {
            log.error("query wechat pay order failed:", e);
            throw new PaymentException("query wechat pay order failed:", e);
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

    @NotNull
    private Map<String, Object> generateStringObjectMap(WechatPayRequest req) {
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
        switch (tradeState) {
            case "SUCCESS":
                return PayConstant.PayStatus.SUCCESS;
            case "REFUND":
                return PayConstant.PayStatus.REFUNDED;
            case "NOTPAY":
                return PayConstant.PayStatus.PENDING;
            case "CLOSED":
                return PayConstant.PayStatus.CLOSED;
            default:
                return PayConstant.PayStatus.FAILED;
        }
    }
}