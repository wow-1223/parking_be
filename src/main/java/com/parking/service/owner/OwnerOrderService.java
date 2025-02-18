package com.parking.service.owner;

import com.parking.model.param.owner.response.EarningsStatisticsResponse;
import com.parking.model.param.owner.response.UsageStatisticsResponse;
import com.parking.service.OrderService;

import java.time.LocalDateTime;

public interface OwnerOrderService extends OrderService {

    /**
     * 获取收益统计
     * @param ownerId ownerId
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收益统计数据
     */
    EarningsStatisticsResponse getEarningsStatistics(Long ownerId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取使用率统计
     * @param ownerId ownerId
     * @param parkingSpotId 车位ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 使用率统计数据
     */
    UsageStatisticsResponse getUsageStatistics(Long ownerId, Long parkingSpotId,
                                               LocalDateTime startDate, LocalDateTime endDate);

}