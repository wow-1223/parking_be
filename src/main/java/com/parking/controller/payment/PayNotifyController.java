package com.parking.controller.payment;

import com.parking.enums.PayTypeEnum;
import com.parking.event.PayNotifyEvent;
import com.parking.factory.PayServiceFactory;
import com.parking.service.payment.PayService;
import com.parking.handler.PayNotifyParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/pay/notify")
public class PayNotifyController {

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    @Autowired
    private PayServiceFactory payServiceFactory;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PayNotifyParser payNotifyParser;

    @PostMapping("/alipay")
    public String handleAliPayNotify(HttpServletRequest request) {
        try {
            // 解析支付宝回调参数
            Map<String, String> params = payNotifyParser.parseAlipayNotify(request);
            log.info("received ali pay notify：{}", params);

            // 验证签名
            PayService payService = payServiceFactory.getPayService(PayTypeEnum.ALIPAY);
            payService.handleNotify(params);

            // 发布支付回调事件
            PayNotifyEvent event = payNotifyParser.buildAlipayNotifyEvent(params);
            eventPublisher.publishEvent(event);

            return SUCCESS;

        } catch (Exception e) {
            log.error("handle ali pay notify failed:", e);
            return FAIL;
        }
    }

    @PostMapping("/wechatpay")
    public String handleWechatPayNotify(
            @RequestBody String notifyData,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Serial") String serialNumber) {
        try {
            // 解析微信支付回调参数
            Map<String, String> params = payNotifyParser.parseWechatPayNotify(
                    notifyData, signature, nonce, timestamp, serialNumber);
            log.info("received wechat pay notify：{}", params);

            // 验证签名
            PayService payService = payServiceFactory.getPayService(PayTypeEnum.WECHAT_PAY);
            payService.handleNotify(params);

            // 发布支付回调事件
            PayNotifyEvent event = payNotifyParser.buildWechatPayNotifyEvent(params);
            eventPublisher.publishEvent(event);

            return SUCCESS;

        } catch (Exception e) {
            log.error("handle wechat pay notify failed:", e);
            return FAIL;
        }
    }

    @PostMapping("/alipay/refund")
    public String handleAlipayRefundNotify(HttpServletRequest request) {
        try {
            // 解析支付宝退款回调参数
            Map<String, String> params = payNotifyParser.parseAlipayNotify(request);
            log.info("received ali pay refund notify：{}", params);

            // 验证签名
            PayService payService = payServiceFactory.getPayService(PayTypeEnum.ALIPAY);
            payService.handleNotify(params);

            // 发布退款回调事件
            PayNotifyEvent event = payNotifyParser.buildAlipayRefundNotifyEvent(params);
            eventPublisher.publishEvent(event);

            return SUCCESS;

        } catch (Exception e) {
            log.error("处理支付宝退款回调失败", e);
            return FAIL;
        }
    }

    @PostMapping("/wechatpay/refund")
    public String handleWechatPayRefundNotify(
            @RequestBody String notifyData,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Serial") String serialNumber) {
        try {
            // 解析微信支付退款回调参数
            Map<String, String> params = payNotifyParser.parseWechatPayNotify(
                    notifyData, signature, nonce, timestamp, serialNumber);
            log.info("received wechat pay refund notify：{}", params);

            // 验证签名
            PayService payService = payServiceFactory.getPayService(PayTypeEnum.WECHAT_PAY);
            payService.handleNotify(params);

            // 发布退款回调事件
            PayNotifyEvent event = payNotifyParser.buildWechatPayRefundNotifyEvent(params);
            if (event == null) {
                return FAIL;
            }
            eventPublisher.publishEvent(event);

            return SUCCESS;

        } catch (Exception e) {
            log.error("handle wechat refund notify failed:", e);
            return FAIL;
        }
    }
}