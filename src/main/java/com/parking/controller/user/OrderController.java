package com.parking.controller.user;

import com.parking.model.dto.*;
import com.parking.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @ApiOperation("获取订单列表")
    @GetMapping
    public PageResponse<OrderListItemDTO> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return orderService.getOrders(status, page, pageSize);
    }

    @ApiOperation("取消订单")
    @PostMapping("/{id}/cancel")
    public CancelOrderResponse cancelOrder(@PathVariable String id) {
        return orderService.cancelOrder(id);
    }
} 