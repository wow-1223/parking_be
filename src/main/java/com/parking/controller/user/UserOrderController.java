package com.parking.controller.user;

import com.parking.model.dto.order.OrderDTO;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.CancelOrderRequest;
import com.parking.model.param.user.request.CreateOrderRequest;
import com.parking.model.param.common.PageResponse;
import com.parking.service.user.UserOrderService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/orders")
public class UserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    @GetMapping("/getOrders")
    public PageResponse<OrderDTO> getOrders(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return userOrderService.getOrders(userId, status, page, size);
    }

    @PostMapping("/createOrder")
    public OperationResponse createOrder(@RequestBody CreateOrderRequest request) {
        return userOrderService.createOrder(request);
    }

    @PostMapping("/cancelOrder")
    public OperationResponse cancelOrder(@RequestBody CancelOrderRequest request) {
        return userOrderService.cancelOrder(request);
    }
} 