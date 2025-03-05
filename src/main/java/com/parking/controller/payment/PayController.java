package com.parking.controller.payment;

import com.parking.enums.PayTypeEnum;
import com.parking.factory.PayServiceFactory;
import com.parking.model.param.payment.request.PayRequest;
import com.parking.model.param.payment.response.PayResponse;
import com.parking.handler.payment.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 支付相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/pay")
public class PayController {

    @Autowired
    private PayServiceFactory payServiceFactory;

    /**
     * 创建支付订单
     * @param request 支付请求参数
     * @return 支付结果
     */
    @PostMapping("/createPayOrder")
    public PayResponse createPayOrder(@RequestBody PayRequest request) {
        log.info("create pay order, request：{}", request);
        PayService payService = payServiceFactory.getPayService(PayTypeEnum.fromValue(request.getPayType()));
        return payService.createOrder(request);
    }

    /**
     * 查询支付订单状态
     * @param orderId 订单号
     * @param payType 支付类型
     * @return 支付结果
     */
    @GetMapping("/query/{orderId}")
    public PayResponse queryOrder(
            @PathVariable String orderId,
            @RequestParam String payType) {
        PayService payService = payServiceFactory.getPayService(PayTypeEnum.fromValue(payType));
        return payService.queryOrder(orderId);
    }

//    @PostMapping("/refund/{orderId}")
//    public PayResponse refund(
//            @PathVariable String orderId,
//            @RequestParam String payType,
//            @RequestParam Long amount,
//            @RequestParam String reason) {
//        PayService payService = payServiceFactory.getPayService(PayTypeEnum.fromValue(payType));
//        return payService.refund(orderId, amount, reason);
//    }
}