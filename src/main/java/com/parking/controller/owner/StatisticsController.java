package com.parking.controller.owner;

import com.parking.model.dto.owner.EarningsStatisticsResponse;
import com.parking.model.dto.owner.UsageStatisticsResponse;
import com.parking.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "出租方统计接口")
@RestController
@RequestMapping("/api/owner/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("获取收益统计")
    @GetMapping("/earnings")
    public EarningsStatisticsResponse getEarningsStatistics(
            @RequestParam String timeRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return statisticsService.getEarningsStatistics(timeRange, startDate, endDate);
    }

    @ApiOperation("获取使用率统计")
    @GetMapping("/usage")
    public UsageStatisticsResponse getUsageStatistics(
            @RequestParam(required = false) String parkingId,
            @RequestParam String timeRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return statisticsService.getUsageStatistics(parkingId, timeRange, startDate, endDate);
    }
} 