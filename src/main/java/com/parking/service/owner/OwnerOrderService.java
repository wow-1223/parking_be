package com.parking.service.owner;

import com.parking.model.param.owner.request.StatisticsRequest;
import com.parking.model.param.owner.response.EarningsStatisticsResponse;
import com.parking.model.param.owner.response.UsageStatisticsResponse;
import com.parking.service.OrderService;

public interface OwnerOrderService extends OrderService {

    /**
     * 获取收益统计
     * @param request request
     * @return 收益统计数据
     */
    EarningsStatisticsResponse getEarningsStatistics(StatisticsRequest request);

    /**
     * 获取使用率统计
     * @param request request
     * @return 使用率统计数据
     */
    UsageStatisticsResponse getUsageStatistics(StatisticsRequest request);

}