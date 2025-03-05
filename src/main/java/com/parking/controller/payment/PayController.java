package com.parking.controller.payment;

import com.parking.enums.PayTypeEnum;
import com.parking.factory.PayServiceFactory;
import com.parking.model.param.payment.request.PayRequest;
import com.parking.model.param.payment.response.PayResponse;
import com.parking.service.payment.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/pay")
public class PayController {

    @Autowired
    private PayServiceFactory payServiceFactory;

    @PostMapping("/createPayOrder")
    public PayResponse createPayOrder(@RequestBody PayRequest request) {
        log.info("create pay order, request：{}", request);
        PayService payService = payServiceFactory.getPayService(PayTypeEnum.fromValue(request.getPayType()));
        return payService.createOrder(request);
    }

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