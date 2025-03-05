package com.parking.controller.owner;

import com.parking.model.dto.order.OrderDTO;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.owner.request.StatisticsRequest;
import com.parking.model.param.owner.response.EarningsStatisticsResponse;
import com.parking.model.param.owner.response.UsageStatisticsResponse;
import com.parking.service.owner.OwnerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 租户订单
 */
@RestController
@RequestMapping("/api/owner/orders")
public class OwnerOrderController {

    @Autowired
    private OwnerOrderService ownerOrderService;

    /**
     * 获取订单列表
     * @param ownerId 租户id
     * @param status  订单状态
     * @param page    页码
     * @param size    每页数量
     * @return 订单列表
     */
    @GetMapping("/getOrders")
    public PageResponse<OrderDTO> getOrders(
            @RequestParam Long ownerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ownerOrderService.getOrders(ownerId, status, page, size);
    }

    /**
     * 获取订单详情
     * @param request 查询条件
     * @return 收益统计
     */
    @PostMapping("/earnings")
    public EarningsStatisticsResponse getEarningsStatistics(@RequestBody StatisticsRequest request) {
        return ownerOrderService.getEarningsStatistics(request);
    }

    /**
     * 获取使用统计
     * @param request 查询条件
     * @return 使用统计
     */
    @PostMapping("/usage")
    public UsageStatisticsResponse getUsageStatistics(@RequestBody StatisticsRequest request) {
        return ownerOrderService.getUsageStatistics(request);
    }
} 