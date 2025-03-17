package com.parking.controller.user;

import com.parking.handler.jwt.TokenUtil;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.CancelOrderRequest;
import com.parking.model.param.user.request.OperateOrderRequest;
import com.parking.model.param.common.PageResponse;
import com.parking.service.user.UserOrderService;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user/orders")
public class UserOrderController {

    @Resource
    private UserOrderService userOrderService;

    @GetMapping("/getOrders")
    public PageResponse<OrderDTO> getOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return userOrderService.getOrders(TokenUtil.getUserId(), status, page, size);
    }

    @PostMapping("/createOrder")
    public OperationResponse createOrder(@RequestBody OperateOrderRequest request) {
        log.info("Create order request: {}", request);
        return userOrderService.createOrder(request);
    }

    @PostMapping("/cancelOrder")
    public OperationResponse cancelOrder(@RequestBody CancelOrderRequest request) {
        log.info("Cancel order request: {}", request);
        return userOrderService.cancelOrder(request);
    }

    /**
     * 确认订单完成
     */
    @PostMapping("/complete")
    public OperationResponse completeOrder(@RequestBody CancelOrderRequest request) {
        log.info("Complete order request: {}", request);
        return userOrderService.completeOrder(request);
    }
} 