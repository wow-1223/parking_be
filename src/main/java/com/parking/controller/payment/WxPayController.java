package com.parking.controller.payment;

import com.parking.model.dto.payment.WxPayRequest;
import com.parking.model.dto.payment.WxPayResponse;
import com.parking.service.payment.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/pay")
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;

    @PostMapping("/create")
    public WxPayResponse createOrder(@RequestBody WxPayRequest request) {
        log.info("创建支付订单，请求参数：{}", request);
        return wxPayService.createOrder(request);
    }

    @PostMapping("/notify")
    public String handlePayNotify(@RequestBody String notifyData,
                                  @RequestHeader("Wechatpay-Signature") String signature,
                                  @RequestHeader("Wechatpay-Nonce") String nonce,
                                  @RequestHeader("Wechatpay-Timestamp") String timestamp,
                                  @RequestHeader("Wechatpay-Serial") String serialNumber) {
        wxPayService.handlePayNotify(notifyData, signature, nonce, timestamp, serialNumber);
        return "success";
    }

    @GetMapping("/query/{orderId}")
    public WxPayResponse queryOrder(@PathVariable String orderId) {
        return wxPayService.queryOrder(orderId);
    }

    @PostMapping("/refund/{orderId}")
    public WxPayResponse refund(@PathVariable String orderId, @RequestParam Long amount, @RequestParam String reason) {
        return wxPayService.refund(orderId, amount, reason);
    }
}