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

    @PostMapping("/earnings")
    public EarningsStatisticsResponse getEarningsStatistics(@RequestBody StatisticsRequest request) {
        return ownerOrderService.getEarningsStatistics(request);
    }

    @PostMapping("/usage")
    public UsageStatisticsResponse getUsageStatistics(@RequestBody StatisticsRequest request) {
        return ownerOrderService.getUsageStatistics(request);
    }
} 