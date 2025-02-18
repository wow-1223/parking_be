package com.parking.controller.owner;

import com.parking.model.dto.order.OrderDTO;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.owner.response.EarningsStatisticsResponse;
import com.parking.model.param.owner.response.UsageStatisticsResponse;
import com.parking.service.owner.OwnerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/owner/orders")
public class OwnerOrderController {

    @Autowired
    private OwnerOrderService ownerOrderService;

    @GetMapping("/getOrders")
    public PageResponse<OrderDTO> getOrders(
            @RequestParam Long ownerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ownerOrderService.getOrders(ownerId, status, page, size);
    }

    @GetMapping("/earnings")
    public EarningsStatisticsResponse getEarningsStatistics(
            @RequestParam Long ownerId,
            @RequestParam String timeRange,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        return ownerOrderService.getEarningsStatistics(ownerId, startDate, endDate);
    }

    @GetMapping("/usage")
    public UsageStatisticsResponse getUsageStatistics(
            @RequestParam Long ownerId,
            @RequestParam(required = false) Long parkingId,
            @RequestParam String timeRange,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        return ownerOrderService.getUsageStatistics(ownerId, parkingId, startDate, endDate);
    }
} 