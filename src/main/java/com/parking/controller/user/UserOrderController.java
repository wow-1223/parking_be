package com.parking.controller.user;

import com.parking.handler.jwt.TokenUtil;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.OperateOrderRequest;
import com.parking.model.param.user.request.CreateOrderRequest;
import com.parking.model.param.common.PageResponse;
import com.parking.service.user.UserOrderService;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * 用户订单相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/user/orders")
public class UserOrderController {

    @Resource
    private UserOrderService userOrderService;

    /**
     * 获取用户订单列表
     * @param status 订单状态
     * @param page  当前页数，默认1
     * @param size  每页数量，默认20
     * @return 订单列表
     */
    @GetMapping("/getOrders")
    public PageResponse<OrderDTO> getOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return userOrderService.getOrders(TokenUtil.getUserId(), status, page, size);
    }

    /**
     * 创建订单
     * @param request 订单创建请求参数
     * @return 订单创建结果
     */
    @PostMapping("/createOrder")
    public OperationResponse createOrder(@RequestBody CreateOrderRequest request) {
        log.info("Create order request: {}", request);
        return userOrderService.createOrder(request);
    }

    /**
     * 取消订单
     * @param request 订单取消请求参数
     * @return 订单取消结果
     */
    @PostMapping("/cancelOrder")
    public OperationResponse cancelOrder(@RequestBody OperateOrderRequest request) {
        log.info("Cancel order request: {}", request);
        return userOrderService.cancelOrder(request);
    }

    /**
     * 确认订单完成
     * @param request 订单完成请求参数
     * @return 订单完成结果
     */
    @PostMapping("/complete")
    public OperationResponse completeOrder(@RequestBody OperateOrderRequest request) {
        log.info("Complete order request: {}", request);
        return userOrderService.completeOrder(request);
    }
} 