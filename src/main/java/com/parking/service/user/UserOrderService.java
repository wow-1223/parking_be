package com.parking.service.user;

import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.user.request.CreateOrderRequest;
import com.parking.service.OrderService;

public interface UserOrderService extends OrderService {

    OperationResponse createOrder(CreateOrderRequest request);

    OperationResponse cancelOrder(Long id);
}