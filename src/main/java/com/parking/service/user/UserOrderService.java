package com.parking.service.user;

import com.parking.model.dto.user.request.CreateOrderRequest;
import com.parking.model.dto.user.response.OrderResponse;
import com.parking.model.dto.common.PageResponse;

public interface UserOrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    
    PageResponse<OrderListItemDTO> getOrders(String status, Integer page, Integer pageSize);
    
    CancelOrderResponse cancelOrder(String id);
} 