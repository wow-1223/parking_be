package com.parking.controller.payment;

import com.parking.enums.PayTypeEnum;
import com.parking.handler.payment.notify.PayNotifyService;
import com.parking.model.vo.pay.PayNotifyVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 回调处理
 *  支付回调：1、解析参数 2、验证签名 3、返回成功或失败 4、异步处理业务逻辑（包含重试机制）
 */
@Slf4j
@RestController
@RequestMapping("/api/pay/notify")
public class PayNotifyController {

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    @Autowired
    private PayNotifyService payNotifyService;

    @PostMapping("/alipay")
    public String handleAliPayNotify(HttpServletRequest request) {
        try {
            // 解析支付宝回调参数
            Map<String, String> params = payNotifyService.parseAlipayNotify(request);
            log.info("received ali pay notify：{}", params);

            // 验证签名
            payNotifyService.verifyNotifySign(PayTypeEnum.ALIPAY, params);

            // 构建回调参数
            PayNotifyVO notify = payNotifyService.generatePayNotifyVO(PayTypeEnum.ALIPAY, params);

            // 异步处理业务逻辑（包含重试机制）
            payNotifyService.handleNotify(notify);

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
            Map<String, String> params = payNotifyService.parseWechatPayNotify(
                    notifyData, signature, nonce, timestamp, serialNumber);

            // 构建回调参数
            PayNotifyVO notify = payNotifyService.generatePayNotifyVO(PayTypeEnum.WECHAT_PAY, params);

            // 异步处理业务逻辑（包含重试机制）
            payNotifyService.handleNotify(notify);

            return SUCCESS;
        } catch (Exception e) {
            log.error("handle wechat pay notify failed:", e);
            return FAIL;
        }
    }

    @PostMapping("/alipay/refund")
    public String handleAlipayRefundNotify(HttpServletRequest request) {
        try {
            // 解析支付宝回调参数
            Map<String, String> params = payNotifyService.parseAlipayNotify(request);
            log.info("received ali refund notify：{}", params);

            // 验证签名
            payNotifyService.verifyNotifySign(PayTypeEnum.ALIPAY, params);

            // 构建回调参数
            PayNotifyVO notify = payNotifyService.generateRefundNotifyVO(PayTypeEnum.ALIPAY, params);

            // 异步处理业务逻辑（包含重试机制）
            payNotifyService.handleNotify(notify);

            return SUCCESS;

        } catch (Exception e) {
            log.error("Process ali pay refund notify failed ", e);
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
            // 解析微信支付回调参数
            Map<String, String> params = payNotifyService.parseWechatPayNotify(
                    notifyData, signature, nonce, timestamp, serialNumber);

            // 构建回调参数
            PayNotifyVO notify = payNotifyService.generateRefundNotifyVO(PayTypeEnum.WECHAT_PAY, params);

            // 异步处理业务逻辑（包含重试机制）
            payNotifyService.handleNotify(notify);

            return SUCCESS;
        } catch (Exception e) {
            log.error("handle wechat pay notify failed:", e);
            return FAIL;
        }
    }
}