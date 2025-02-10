package com.parking.service.user;

import com.parking.model.dto.OrderResponse;
import com.parking.model.dto.user.request.CreateOrderRequest;
import com.parking.model.dto.common.PageResponse;
import com.parking.model.dto.user.response.CancelOrderResponse;
import com.parking.model.dto.OrderListItemDTO;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    
    PageResponse<OrderListItemDTO> getOrders(String status, Integer page, Integer pageSize);
    
    CancelOrderResponse cancelOrder(String id);
} 