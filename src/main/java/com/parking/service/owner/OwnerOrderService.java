package com.parking.service;

import com.parking.model.dto.PageResponse;
import com.parking.model.dto.owner.OwnerOrderListItemDTO;

public interface OwnerOrderService {
    /**
     * 获取订单列表
     * @param status 订单状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 订单列表
     */
    PageResponse<OwnerOrderListItemDTO> getOrders(String status, Integer page, Integer pageSize);
} 