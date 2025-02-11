package com.parking.controller.owner;

import com.parking.model.dto.owner.response.EarningsStatisticsResponse;
import com.parking.model.dto.owner.response.UsageStatisticsResponse;
import com.parking.service.owner.StatisticsService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner/statistics")
public class OwnerStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/earnings")
    public EarningsStatisticsResponse getEarningsStatistics(
            @RequestParam String timeRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return statisticsService.getEarningsStatistics(timeRange, startDate, endDate);
    }

    @GetMapping("/usage")
    public UsageStatisticsResponse getUsageStatistics(
            @RequestParam(required = false) String parkingId,
            @RequestParam String timeRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return statisticsService.getUsageStatistics(parkingId, timeRange, startDate, endDate);
    }
} 