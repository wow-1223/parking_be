package com.parking.service.owner;

import com.parking.model.dto.owner.response.EarningsStatisticsResponse;
import com.parking.model.dto.owner.response.UsageStatisticsResponse;

public interface StatisticsService {
    /**
     * 获取收益统计
     * @param timeRange 时间范围
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收益统计数据
     */
    EarningsStatisticsResponse getEarningsStatistics(String timeRange, String startDate, String endDate);
    
    /**
     * 获取使用率统计
     * @param parkingId 车位ID
     * @param timeRange 时间范围
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 使用率统计数据
     */
    UsageStatisticsResponse getUsageStatistics(String parkingId, String timeRange, 
            String startDate, String endDate);
} 