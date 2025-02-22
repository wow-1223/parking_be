package com.parking.service;

import com.parking.model.dto.order.OrderDTO;
import com.parking.model.dto.order.OrderDetailDTO;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;

public interface OrderService {

    PageResponse<OrderDTO> getOrders(Long id, Integer status, Integer page, Integer size);

    DetailResponse<OrderDetailDTO> getOrderDetail(Long id);
}
