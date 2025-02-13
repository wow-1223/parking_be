package com.parking.service.owner;

import com.parking.model.dto.common.PageResponse;
import com.parking.model.dto.owner.OwnerOrderListItemDTO;
import com.parking.model.vo.UserOrderVo;

public interface OwnerOrderService {
    /**
     * 获取订单列表
     * @param status 订单状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 订单列表
     */
    PageResponse<OwnerOrderListItemDTO> getOrders(String status, Integer page, Integer pageSize);

    PageResponse<UserOrderVo> getUserOrders(String status, Integer page, Integer pageSize);
} 