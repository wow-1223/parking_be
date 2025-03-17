package com.parking.service2.user;

import com.parking.enums.user.UserRoleEnum;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.dto.order.OrderDetailDTO;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;

public interface OrderService {

    PageResponse<OrderDTO> getOrders(Long id, UserRoleEnum role, Integer status, Integer page, Integer size);

    DetailResponse<OrderDetailDTO> getOrderDetail(Long orderId);

}
