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
import com.parking.constant.PayConstant;
import com.parking.enums.PayTypeEnum;
import com.parking.exception.PaymentException;
import com.parking.service.payment.PayService;
import com.parking.handler.task.ThreadPoolUtil;
import com.parking.model.param.payment.request.PayRequest;
import com.parking.model.param.payment.response.AliPayResponse;
import com.parking.model.param.payment.response.PayResponse;
import com.parking.model.vo.pay.PayNotifyVO;
import com.parking.util.JsonUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AliPayServiceImpl implements PayService {

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ThreadPoolUtil threadPoolUtil;

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

    @Override
    public PayResponse createOrder(PayRequest request) {
        try {
//            AlipayTradeWapPayRequest alipayRequest = generateAlipayTradeWapPayRequest(request);
//            AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest);
//            if (!response.isSuccess()) {
//                throw new PaymentException("create ali pay order failed: " + response.getSubMsg());
//            }
//            AliPayResponse payResponse = new AliPayResponse();
//            payResponse.setOrderId(request.getOrderId());
//            payResponse.setPayUrl(response.getBody());
//            return payResponse;
//        } catch (AlipayApiException e) {
//            log.error("create ali pay order failed: ", e);
//            throw new PaymentException("create ali pay order failed: ", e);
//        }

            // todo for test
            AlipayTradeWapPayResponse response = new AlipayTradeWapPayResponse();
            response.setBody("test_url");
            AliPayResponse payResponse = new AliPayResponse();
            payResponse.setOrderId(request.getOrderId());
            payResponse.setPayUrl(response.getBody());
            callNotifyForTest(request);
            return payResponse;
        } catch (Exception e) {
            log.error("create ali pay order failed: ", e);
            throw new PaymentException("create ali pay order failed: ", e);
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

    @Override
    public void verifyNotifySign(Map<String, String> params) {
        try {
            // 验证签名
//            boolean signVerified = verifySign(params);


            // todo for test
            boolean signVerified = true;
            if (!signVerified) {
                throw new PaymentException("ali pay notify sign verify failed");
            }
        } catch (Exception e) {
            log.error("verify ali pay notify sign failed:", e);
            throw new PaymentException("verify ali pay notify sign failed:", e);
        }
    }

    @Override
    public PayNotifyVO generatePayNotifyVO(Map<String, String> params) {
        // 回调字段 https://opendocs.alipay.com/open/203/105286
        String orderId = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        String totalAmount = params.get("total_amount");
        String notifyTime = params.get("gmt_payment");

        // 转换支付状态
        String status = "TRADE_SUCCESS".equals(tradeStatus) ?
                PayConstant.PayStatus.SUCCESS : PayConstant.PayStatus.PENDING;

        // 转换金额为分
        Long amount = Math.round(Double.parseDouble(totalAmount) * 100);

        return new PayNotifyVO(orderId, tradeNo, status, amount, notifyTime, PayTypeEnum.ALIPAY);
    }

    @Override
    public PayNotifyVO generateRefundNotifyVO(Map<String, String> params) {
        String orderId = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String refundAmount = params.get("refund_fee");
        String notifyTime = params.get("gmt_refund");

        // 转换金额为分
        Long amount = Math.round(Double.parseDouble(refundAmount) * 100);

        return new PayNotifyVO(orderId, tradeNo, PayConstant.PayStatus.REFUNDED, amount, notifyTime, PayTypeEnum.ALIPAY);
    }


    private AlipayTradeWapPayRequest generateAlipayTradeWapPayRequest(PayRequest request) {
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());

        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(request.getOrderId());
        model.setTotalAmount(String.valueOf(request.getAmount() / 100.0)); // 转换为元
        model.setSubject(request.getDescription());
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
        return switch (tradeStatus) {
            case "TRADE_SUCCESS" -> "SUCCESS";
            case "TRADE_CLOSED" -> "CLOSED";
            case "TRADE_FINISHED" -> "FINISHED";
            default -> "PENDING";
        };
    }

    private void callNotifyForTest(PayRequest request) {
        // 触发回调逻辑
        threadPoolUtil.executeAsync(() -> {
            log.info("Executing async task: call ali pay notify for test");
            // 异步任务逻辑
            try {
                // 等待3秒模拟支付过程
                Thread.sleep(3000);

                // 构造回调参数
                Map<String, String> params = new HashMap<>();
                params.put("out_trade_no", request.getOrderId());
                params.put("trade_no", generateTradeNo());  // 生成支付宝交易号
                params.put("trade_status", "TRADE_SUCCESS");
                params.put("total_amount", String.valueOf(request.getAmount() / 100.0));
                params.put("gmt_payment", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                // 添加签名
                String privateKey = alipayConfig.getPrivateKey();
                String content = AlipaySignature.getSignContent(params);
//                String sign = AlipaySignature.rsa256Sign(content, privateKey, alipayConfig.getCharset());
                String sign = "alipay_rsa256_sign_" + privateKey + "_" + content;
                params.put("sign", sign);

                // 发送HTTP请求到回调接口
                String notifyUrl = alipayConfig.getNotifyUrl();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                params.forEach(map::add);

                HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(map, headers);

                ResponseEntity<String> notifyResponse = restTemplate.postForEntity(notifyUrl, req, String.class);

//                HttpServletRequest req = new HttpServletRequestWrapper() {
//                    @Override
//                    public String getParameter(String name) {
//                        return params.get(name);
//                    }
//                }
//                String res = payNotifyController.handleAliPayNotify(req);

                log.info("Async notify result: {}", notifyResponse.getBody());
            } catch (Exception e) {
                log.error("Failed to send async notify", e);
            }
        });
    }

    // 生成模拟的支付宝交易号
    private String generateTradeNo() {
        return "T" + System.currentTimeMillis() + RandomStringUtils.randomNumeric(10);
    }
}