package com.parking.controller.user;

import com.parking.model.dto.OrderResponse;
import com.parking.model.dto.user.request.CreateOrderRequest;
import com.parking.model.dto.common.PageResponse;
import com.parking.model.dto.user.response.CancelOrderResponse;
import com.parking.model.dto.OrderListItemDTO;
import com.parking.service.user.OrderService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/createOrder")
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/getOrders")
    public PageResponse<OrderListItemDTO> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return orderService.getOrders(status, page, pageSize);
    }

    @PostMapping("/{id}/cancel")
    public CancelOrderResponse cancelOrder(@PathVariable String id) {
        return orderService.cancelOrder(id);
    }
} 